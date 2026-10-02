#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Resolve a (machine, features[, target]) triple against ci/build-matrix.yaml and turn the
kas/*.yml fragments (kept as this repo's data format) into a bitbake-setup configuration.

bitbake-setup cannot express "this feature needs these extra repos/layers" in a choice menu,
so instead of a static JSON per combination this script computes one: the kas fragments
(base + machine + features [+ ci-only, sstate-shared] + pins/floating) are merged exactly the
way kas merged them (includes first, later files override, repo/layer insertion order kept),
and the result is rendered as

  * `sources`      every repo with a url (git-remote, pinned `rev`) and every in-repo layer
                   root (`local`, symlinked into layers/ - the only way to keep ONE ordered
                   BBLAYERS list, which bitbake-setup's bb-layers/bb-layers-file-relative
                   split would otherwise reorder),
  * `bb-layers`    in kas order,
  * `oe-fragments` `machine/<m>`, `distro/<d>` (builtin) and one `agl-setup/<key>` per
                   kas `local_conf_header` block (files in meta-agl-setup/conf/fragments/agl,
                   generated from the kas/*.yml by --write-fragments, checked by --check-fragments).

The generated JSON is written to <repo>/.bbsetup-<setup-name>.conf.json (repo root on purpose:
stable path, so `bitbake-setup update` re-reads it and picks up changes).

Fields (--fields a,b,c prints one value per line): setup-name, config (path of the written
JSON), fragments, or any ci/build-matrix.yaml entry field (target, sdk, ...).

--no-matrix skips the matrix lookup (free-form scripts/aglsetup.sh use).
--validate-base builds the bare oe-core config used only by validate.sh's yocto-check-layer.
--work-dir is the repo path as seen by bitbake-setup (the container mount point, /work);
defaults to the repo root.
AGL_FLOATING=1 floats on branch tips instead of kas/pins.yml; CI=true adds kas/ci-only.yml;
AGL_SSTATE_DIR set adds kas/local/sstate-shared.yml (same triggers the kas scripts had).
"""
import argparse
import copy
import json
import os
import re
import sys
from pathlib import Path

import yaml

sys.path.insert(0, str(Path(__file__).resolve().parent))
from _matrix import expand_entries  # noqa: E402

REPO_ROOT = Path(__file__).resolve().parents[2]
FRAGMENT_LAYER = "meta-agl-setup"
FRAGMENT_COLLECTION = "agl-setup"
FRAGMENT_PREFIX = f"{FRAGMENT_COLLECTION}/agl/"   # <collection>/<subdir under conf/fragments>/
FRAGMENT_DIR = REPO_ROOT / FRAGMENT_LAYER / "conf" / "fragments" / "agl"
FRAGMENT_NOTE = "# Generated from kas/*.yml by ci/scripts/_compose_setup.py --write-fragments. Do not edit."


def find_entries(machine, features):
    wanted = sorted(features)
    try:
        entries = expand_entries()
    except ValueError as exc:
        sys.exit(f"error: {exc}")
    return [e for e in entries
            if e["machine"] == machine and sorted(e.get("features", [])) == wanted]


# --- kas-compatible merging -------------------------------------------------------------

def _merge(dst, src):
    """kas-style recursive dict merge: src wins, dst's key order is kept."""
    for key, val in src.items():
        if isinstance(val, dict) and isinstance(dst.get(key), dict):
            _merge(dst[key], val)
        elif val is None and key in dst:
            continue
        else:
            dst[key] = copy.deepcopy(val)


def _load_into(merged, relpath, seen_stack=()):
    path = REPO_ROOT / relpath
    if not path.exists():
        sys.exit(f"error: {relpath} does not exist")
    if relpath in seen_stack:
        sys.exit(f"error: include cycle at {relpath}")
    data = yaml.safe_load(path.read_text()) or {}
    # Includes are applied first, so the including file overrides them (kas semantics).
    for inc in (data.get("header") or {}).get("includes", []) or []:
        _load_into(merged, inc, seen_stack + (relpath,))
    body = {k: v for k, v in data.items() if k != "header"}
    _merge(merged, body)


def merge_kas_files(files):
    merged = {}
    for f in files:
        _load_into(merged, f)
    return merged


def kas_files(machine, features, extra_features, ci, sstate, floating):
    files = ["kas/base.yml", f"kas/machine/{machine}.yml"]
    files += [f"kas/feature/{f}.yml" for f in list(features) + list(extra_features)]
    if sstate:
        files.append("kas/local/sstate-shared.yml")
    if ci:
        files.append("kas/ci-only.yml")
    files.append("kas/floating.yml" if floating else "kas/pins.yml")
    return files


def setup_name(machine, features, extra_features):
    return "-".join([machine] + list(features) + list(extra_features))


# --- rendering ------------------------------------------------------------------------

def _repo_layers(repo):
    """[(name, prio)] of one merged kas repo entry, like kas's RepoLayer list."""
    if "layers" not in repo:
        return [("", 0)]       # kas: no `layers:` key -> the repo root is the one layer
    out = []
    for name, prop in (repo["layers"] or {}).items():   # `layers: {}` -> not a layer (bitbake)
        if prop in ("disabled", "excluded", "n", "no", "0", "false", 0):
            continue
        out.append((name, prop.get("prio", 0) if isinstance(prop, dict) else 0))
    return out


def render(merged, work_dir, machine_default=None):
    repos = merged.get("repos", {})
    pins = (merged.get("overrides") or {}).get("repos", {})
    sources, layer_entries = {}, []
    fragments = []

    local_conf = merged.get("local_conf_header") or {}
    # kas's bblayers_conf_header (LCONF_VERSION = "6") is deliberately dropped: it only made
    # oe-core's sanity check auto-migrate kas's generated bblayers.conf to the current
    # version (7). bitbake-setup's bblayers.conf carries no LCONF_VERSION line and sanity
    # accepts that; setting the variable anyway makes the migration crash on the missing line.

    # bitbake first: bitbake-setup needs it, and oe-init-build-env expects it next to oe-core.
    ordered = sorted(repos, key=lambda r: r != "bitbake")
    for name in ordered:
        repo = repos[name] or {}
        path = repo.get("path", name)
        if repo.get("url"):
            branch = repo["branch"]
            rev = pins.get(name, {}).get("commit", branch)
            sources[name] = {"git-remote": {"uri": repo["url"], "branch": branch, "rev": rev},
                             "path": path}
        else:
            sources[name] = {"local": {"path": str(Path(work_dir) / path)}, "path": path}
        for lname, prio in _repo_layers(repo):
            layer_entries.append((-prio, name, lname, path if lname == "" else f"{path}/{lname}"))

    # kas writes bblayers.conf sorted by (priority desc, repo name, layer name) - NOT in merge
    # order - and `layers: {}` repos (bitbake itself) contribute no layer. BBLAYERS order
    # decides BBPATH/bbappend order, so reproduce it exactly.
    bb_layers = [e[3] for e in sorted(layer_entries)]
    if local_conf:
        sources[FRAGMENT_LAYER] = {"local": {"path": str(Path(work_dir) / FRAGMENT_LAYER)},
                                   "path": FRAGMENT_LAYER}
        bb_layers.insert(0, FRAGMENT_LAYER)
        # kas writes local.conf blocks sorted by key (config.py _get_conf_header), not in merge
        # order; order matters (e.g. AGL_FEATURES += ... append order), so reproduce it.
        fragments += [f"{FRAGMENT_PREFIX}{k}" for k in sorted(local_conf) if (local_conf[k] or "").strip()]

    builtin = []
    if merged.get("machine"):
        builtin.append(f"machine/{merged['machine']}")
    if merged.get("distro"):
        builtin.append(f"distro/{merged['distro']}")

    env = list((merged.get("env") or {}).keys())
    return sources, bb_layers, builtin + fragments, env


def build_config(files, work_dir, name, eula_machine=None):
    merged = merge_kas_files(files)
    sources, bb_layers, fragments, env = render(merged, work_dir)
    if eula_machine:
        env.append("EULA_" + re.sub("-", "_", eula_machine).upper())
    config = {
        "name": "agl",
        "description": f"AGL ({name}) generated from kas/*.yml by ci/scripts/_compose_setup.py",
        "bb-layers": bb_layers,
        "oe-fragments": fragments,
    }
    if env:
        config["bb-env-passthrough-additions"] = env
    return {
        "description": f"AGL {name}",
        "sources": sources,
        "bitbake-setup": {"configurations": [config]},
        "version": "1.0",
    }


def write_config(data, name):
    out = REPO_ROOT / f".bbsetup-{name}.conf.json"
    text = json.dumps(data, indent=4) + "\n"
    if not out.exists() or out.read_text() != text:   # keep mtime stable when unchanged
        out.write_text(text)
    return out


# --- fragment generation from kas local_conf_header blocks -------------------------------

def _fragment_blocks():
    blocks = {}   # fragment file stem -> (text, origin)
    for f in sorted((REPO_ROOT / "kas").rglob("*.yml")):
        data = yaml.safe_load(f.read_text()) or {}
        rel = str(f.relative_to(REPO_ROOT))
        for prefix, section in (("", "local_conf_header"),):
            for key, text in (data.get(section) or {}).items():
                stem = prefix + key
                if stem in blocks and blocks[stem][0] != text:
                    sys.exit(f"error: kas block '{key}' ({section}) differs between "
                             f"{blocks[stem][1]} and {rel}; give one of them another key")
                blocks[stem] = (text, rel)
    return blocks


def _fragment_text(stem, text, origin):
    # DISTRO is set by the builtin `distro/<name>` fragment; a plain assignment would make
    # bitbake abort ("builtin fragment used while DISTRO already has an assignment").
    lines = [ln for ln in text.splitlines() if not re.match(r'\s*DISTRO\s*=', ln)]
    # `bitbake-config-build enable-fragment` (run by bitbake-setup) parses EVERY fragment of
    # every layer standalone, including those of features whose layer isn't in this setup, so a
    # `require` of a feature layer's .inc would abort it. `include` is the soft form; --check-
    # fragments verifies statically that each included file exists in some vendored layer.
    lines = [re.sub(r'^(\s*)require(\s)', r'\1include\2', ln) for ln in lines]
    body = "\n".join(lines).rstrip("\n") + "\n"
    return (f"{FRAGMENT_NOTE}\n# Source: {origin} ({stem})\n"
            f'BB_CONF_FRAGMENT_SUMMARY = "AGL {stem} (from {origin})"\n'
            f'BB_CONF_FRAGMENT_DESCRIPTION = "Generated from the kas {stem} block of {origin}."\n\n{body}')


def fragment_files():
    return {FRAGMENT_DIR / f"{stem}.conf": _fragment_text(stem, text, origin)
            for stem, (text, origin) in _fragment_blocks().items()}


def write_fragments():
    FRAGMENT_DIR.mkdir(parents=True, exist_ok=True)
    want = fragment_files()
    for p in FRAGMENT_DIR.glob("*.conf"):
        if p not in want:
            p.unlink()
    for p, text in want.items():
        p.write_text(text)
    print(f"wrote {len(want)} fragments to {FRAGMENT_DIR.relative_to(REPO_ROOT)}")


def _check_includes(want):
    """Every `include <path>` in a fragment must resolve to a file in some vendored layer."""
    bad = []
    layer_roots = [p.parent.parent for p in REPO_ROOT.glob("meta-*/**/conf/layer.conf")
                   if "external" not in p.parts]
    for frag, text in want.items():
        for m in re.finditer(r'^include\s+(\S+)', text, re.MULTILINE):
            rel = m.group(1)
            if "${" in rel:
                continue
            if not any((root / rel).exists() for root in layer_roots):
                bad.append(f"{frag.name}: {rel}")
    if bad:
        sys.exit("error: fragment includes not found in any vendored layer: " + ", ".join(bad))


def check_fragments():
    want = fragment_files()
    _check_includes(want)
    have = {p: p.read_text() for p in FRAGMENT_DIR.glob("*.conf")} if FRAGMENT_DIR.exists() else {}
    bad = [str(p.relative_to(REPO_ROOT)) for p in sorted(set(want) | set(have))
           if want.get(p) != have.get(p)]
    if bad:
        sys.exit("error: fragments out of sync with kas/*.yml (run `python3 "
                 "ci/scripts/_compose_setup.py --write-fragments`): " + ", ".join(bad))
    print(f"fragments in sync ({len(want)})")


# --- CLI ------------------------------------------------------------------------------------

def main():
    ap = argparse.ArgumentParser(description=__doc__.split("\n\n")[0])
    ap.add_argument("--machine")
    ap.add_argument("--features", default="", help="comma-separated feature list")
    ap.add_argument("--target", help="disambiguate when several images share machine+features")
    ap.add_argument("--extra-features", default="", help="local-only extras (e.g. agl-devel), not matrix-curated")
    ap.add_argument("--fields", default="config", help="comma-separated fields to print")
    ap.add_argument("--no-matrix", action="store_true")
    ap.add_argument("--validate-base", action="store_true")
    ap.add_argument("--work-dir", default=str(REPO_ROOT))
    ap.add_argument("--bitbake-source", action="store_true",
                    help="print the pinned bitbake url and revision (bitbake-setup bootstrap)")
    ap.add_argument("--write-fragments", action="store_true")
    ap.add_argument("--check-fragments", action="store_true")
    args = ap.parse_args()

    if args.write_fragments:
        return write_fragments()
    if args.check_fragments:
        return check_fragments()

    floating = bool(os.environ.get("AGL_FLOATING"))
    if args.bitbake_source:
        repo = merge_kas_files(["kas/base.yml", "kas/pins.yml"])["repos"]["bitbake"]
        pin = merge_kas_files(["kas/base.yml", "kas/pins.yml"])["overrides"]["repos"]["bitbake"]
        print(repo["url"], pin["commit"], sep="\n")
        return
    if args.validate_base:
        files = ["kas/_validate-base.yml", "kas/floating.yml" if floating else "kas/pins.yml"]
        name = "validate-base"
        eula = None
        entry = {}
    else:
        if not args.machine:
            ap.error("--machine is required")
        features = [f for f in args.features.split(",") if f]
        extra = [f for f in args.extra_features.split(",") if f]
        entry = {}
        if not args.no_matrix:
            matches = find_entries(args.machine, features)
            if args.target:
                matches = [e for e in matches if e.get("target") == args.target]
            if not matches:
                sys.exit(f"error: no ci/build-matrix.yaml entry for machine={args.machine} "
                         f"features={features!r}" + (f" target={args.target}" if args.target else "")
                         + " - add one to ci/build-matrix.yaml before building this combination.")
            if len(matches) > 1:
                sys.exit(f"error: {len(matches)} ci/build-matrix.yaml entries match machine={args.machine} "
                         f"features={features!r} - pass --target to disambiguate. Candidates: "
                         + ", ".join(sorted(e.get("target", "?") for e in matches)))
            entry = matches[0]
        files = kas_files(args.machine, features, extra,
                          ci=os.environ.get("CI") == "true",
                          sstate=bool(os.environ.get("AGL_SSTATE_DIR")),
                          floating=floating)
        name = setup_name(args.machine, features, extra)
        eula = args.machine

    data = build_config(files, args.work_dir, name, eula)
    out = write_config(data, name)
    values = {
        "config": str(out),
        "setup-name": name,
        "fragments": " ".join(data["bitbake-setup"]["configurations"][0]["oe-fragments"]),
    }
    for field in args.fields.split(","):
        print(values[field] if field in values else entry.get(field, ""))


if __name__ == "__main__":
    main()
