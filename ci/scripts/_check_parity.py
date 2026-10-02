#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Parity check: for matrix entries, compare what `kas dump` (kas's own merge) says with the
bitbake-setup config _compose_setup.py generates - repos (url/branch/commit/path), ordered
layer list, machine/distro, and local_conf_header block order.

Usage: ci/scripts/_check_parity.py [--all | machine features target]
Needs the `kas` CLI only for this check (not for building)."""
import json
import subprocess
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parent))
import _compose_setup as cs  # noqa: E402
from _matrix import expand_entries  # noqa: E402


def kas_view(files):
    out = subprocess.run(["kas", "dump", "--format", "json", ":".join(files)],
                         cwd=cs.REPO_ROOT, capture_output=True, text=True, check=True).stdout
    return json.JSONDecoder().raw_decode(out[out.index("{"):])[0]


def kas_layers(d):
    """Layer order exactly as kas writes bblayers.conf: kas's own RepoLayer sort."""
    from kas.repos import RepoLayer
    layers = []
    for name, repo in d["repos"].items():
        path = repo.get("path") or name
        entries = repo.get("layers", {"": None})
        for lname, prop in (entries or {}).items():
            if prop in ("disabled", "excluded", "n", "no", "0", "false", 0):
                continue
            prio = prop.get("prio", 0) if isinstance(prop, dict) else 0
            layers.append(RepoLayer(name=lname, priority=prio, repo_name=name, repo_path=Path(path)))
    return [str(layer.path) for layer in sorted(layers)]


def check(machine, features):
    files = cs.kas_files(machine, features, [], ci=False, sstate=False, floating=False)
    kas = kas_view(files)
    ours = cs.build_config(files, str(cs.REPO_ROOT), cs.setup_name(machine, features, []))
    cfg = ours["bitbake-setup"]["configurations"][0]
    errs = []
    want = kas_layers(kas)
    got = [layer for layer in cfg["bb-layers"] if layer != cs.FRAGMENT_LAYER]
    if want != got:
        errs.append(f"layer order differs:\n  kas : {want}\n  ours: {got}")
    pins = (kas.get("overrides") or {}).get("repos", {})
    for name, repo in kas["repos"].items():
        src = ours["sources"].get(name)
        if not src:
            errs.append(f"repo {name} missing in sources")
        elif repo.get("url"):
            g = src["git-remote"]
            if (g["uri"], g["branch"], g["rev"]) != (repo["url"], repo["branch"], pins.get(name, {}).get("commit", repo["branch"])):
                errs.append(f"repo {name}: {g} vs kas {repo.get('url')} {repo.get('branch')} {repo.get('commit')}")
    frags = [f[len(cs.FRAGMENT_PREFIX):] for f in cfg["oe-fragments"] if f.startswith(cs.FRAGMENT_PREFIX)]
    kas_blocks = sorted(kas.get("local_conf_header", {}))   # kas writes local.conf sorted by key
    if frags != kas_blocks:
        errs.append(f"local_conf_header order differs: {frags} vs {kas_blocks}")
    if f"machine/{kas['machine']}" not in cfg["oe-fragments"]:
        errs.append("machine fragment missing")
    return errs


def main():
    seen, bad = set(), 0
    for e in expand_entries():
        key = (e["machine"], tuple(sorted(e.get("features", []))))
        if key in seen:
            continue
        seen.add(key)
        errs = check(e["machine"], list(e.get("features", [])))
        print(("FAIL " if errs else "ok   ") + f"{e['machine']} {','.join(key[1]) or '-'}")
        for x in errs:
            print("   ", x)
        bad += bool(errs)
    sys.exit(1 if bad else 0)


if __name__ == "__main__":
    main()
