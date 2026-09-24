#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Resolve a (machine, features[, target]) triple against ci/build-matrix.yaml and print
either the colon-joined kas file list (default) or a single requested field (--field NAME),
for consumption by the shell scripts in ci/scripts/ and by the Makefile.

No per-combination kas file exists: the list is computed directly as
ci/kas/base.yml:ci/kas/machine/<machine>.yml:ci/kas/feature/<f1>.yml:...:ci/kas/pins.yml -
ci/kas/pins.yml (a single consolidated file pinning every external repo's commit) is always
appended last, so it never needs a per-combination lockfile.

If the AGL_FLOATING env var is set, ci/kas/floating.yml (no commit overrides - every repo floats
to the tip of its declared branch) is appended instead of ci/kas/pins.yml. Local-only; CI never
sets this.

--target disambiguates when several images share the same (machine, features) - e.g. all 5
agl-demo images on one machine. It's required whenever more than one matrix entry matches;
omit it when only one does (unambiguous, matches pre-existing single-image usage).

--extra-features appends extra ci/kas/feature/*.yml fragments on top of a matched matrix
entry's own features, WITHOUT affecting the matrix lookup itself - for local-only additions
like agl-devel (passwordless login) that are deliberately not part of any curated matrix
entry. Never used in CI.
"""
import argparse
import os
import sys
from pathlib import Path

# ci/scripts/ isn't a Python package (no __init__.py, deliberately - these are standalone CLI
# scripts, not a library), so importing the sibling _matrix.py needs its directory on sys.path
# first. Must happen before the import, hence the noqa (import-not-at-top-of-file).
sys.path.insert(0, str(Path(__file__).resolve().parent))
from _matrix import expand_entries  # noqa: E402

REPO_ROOT = Path(__file__).resolve().parents[2]


def find_entries(machine, features):
    wanted = sorted(features)
    try:
        entries = expand_entries()
    except ValueError as exc:
        print(f"error: {exc}", file=sys.stderr)
        sys.exit(1)
    return [
        entry for entry in entries
        if entry["machine"] == machine and sorted(entry.get("features", [])) == wanted
    ]


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--machine", required=True)
    ap.add_argument("--features", default="", help="comma-separated feature list")
    ap.add_argument("--target", help="disambiguate when several images share the same machine+features")
    ap.add_argument("--extra-features", default="", help="comma-separated local-only extra features (e.g. agl-devel), not matrix-curated")
    ap.add_argument("--field", help="print a single matrix entry field instead of the kas file list")
    args = ap.parse_args()

    features = [f for f in args.features.split(",") if f]
    extra_features = [f for f in args.extra_features.split(",") if f]
    matches = find_entries(args.machine, features)
    if args.target:
        matches = [e for e in matches if e.get("target") == args.target]

    if not matches:
        print(
            f"error: no ci/build-matrix.yaml entry for machine={args.machine} "
            f"features={features!r}"
            + (f" target={args.target}" if args.target else "")
            + " - add one to ci/build-matrix.yaml (and any missing ci/kas/feature/*.yml "
            "fragment) before building this combination.",
            file=sys.stderr,
        )
        sys.exit(1)
    if len(matches) > 1:
        targets = ", ".join(sorted(e.get("target", "?") for e in matches))
        print(
            f"error: {len(matches)} ci/build-matrix.yaml entries match machine={args.machine} "
            f"features={features!r} - pass --target to disambiguate. Candidates: {targets}",
            file=sys.stderr,
        )
        sys.exit(1)
    entry = matches[0]

    if args.field:
        print(entry.get(args.field, ""))
        return

    kasfiles = ["ci/kas/base.yml", f"ci/kas/machine/{args.machine}.yml"]
    kasfiles += [f"ci/kas/feature/{f}.yml" for f in features]
    kasfiles += [f"ci/kas/feature/{f}.yml" for f in extra_features]
    kasfiles.append("ci/kas/floating.yml" if os.environ.get("AGL_FLOATING") else "ci/kas/pins.yml")
    for f in kasfiles:
        if not (REPO_ROOT / f).exists():
            print(f"error: {f} does not exist", file=sys.stderr)
            sys.exit(1)
    print(":".join(kasfiles))


if __name__ == "__main__":
    main()
