#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Resolves every external repo's latest commit on its declared branch (git ls-remote) and
updates kas/pins.yml in place with whatever changed, via targeted text substitution (keeps
pins.yml's existing comments/grouping intact - no full YAML round-trip, which would lose them).
Prints a summary of what changed. Review the resulting diff before committing - this can pull
in real upstream breakage, same as any dependency bump.

Usage: ci/scripts/pin-update-helper.py [--machine M --features a,b] [--dry-run]
  no --machine: every repo declared by any kas/{base,machine/*,feature/*}.yml (make pin-update)
  --machine:    only the repos of that one combination, dry-run only (make lock)
"""
import argparse
import re
import subprocess
import sys
from pathlib import Path

import yaml

sys.path.insert(0, str(Path(__file__).resolve().parent))
import _compose_setup as cs  # noqa: E402

PINS_FILE = cs.REPO_ROOT / "kas" / "pins.yml"


def declared_repos(machine=None, features=()):
    """{repo: (url, branch)} for the selected combination, or for every kas fragment."""
    if machine:
        files = cs.kas_files(machine, features, [], ci=False, sstate=False, floating=True)
        repos = cs.merge_kas_files(files).get("repos", {})
    else:
        repos = {}
        for f in ["kas/base.yml", *sorted(str(p.relative_to(cs.REPO_ROOT))
                                           for p in (cs.REPO_ROOT / "kas").glob("*/*.yml"))]:
            if f.startswith("kas/local/"):
                continue
            cs._merge(repos, (yaml.safe_load((cs.REPO_ROOT / f).read_text()) or {}).get("repos", {}))
    return {n: (r["url"], r["branch"]) for n, r in repos.items() if r and r.get("url")}


def resolve(url, branch):
    out = subprocess.run(["git", "ls-remote", url, f"refs/heads/{branch}"],
                         capture_output=True, text=True, check=True).stdout.split()
    if not out:
        sys.exit(f"pin-update-helper: {url} has no branch {branch}")
    return out[0]


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--machine")
    ap.add_argument("--features", default="")
    ap.add_argument("--dry-run", action="store_true", help="show what would change, don't write pins.yml")
    args = ap.parse_args()

    dry = args.dry_run or bool(args.machine)
    repos = declared_repos(args.machine, [f for f in args.features.split(",") if f])
    new_commits = {name: resolve(url, branch) for name, (url, branch) in repos.items()}

    pins_text = PINS_FILE.read_text()
    changed, unknown = {}, []
    for repo, new_commit in new_commits.items():
        m = re.search(rf"^(    {re.escape(repo)}:\n      commit: )([0-9a-f]+)$", pins_text, re.MULTILINE)
        if not m:
            unknown.append(repo)
            continue
        old_commit = m.group(2)
        if old_commit != new_commit:
            changed[repo] = (old_commit, new_commit)
            pins_text = pins_text[: m.start(2)] + new_commit + pins_text[m.end(2):]

    if unknown:
        print(f"pin-update-helper: {len(unknown)} repo(s) resolved but not found in "
              f"{PINS_FILE.relative_to(cs.REPO_ROOT)} (add them there first if they're meant to be "
              f"pinned): {', '.join(sorted(unknown))}", file=sys.stderr)

    if not changed:
        print("pin-update-helper: everything already at the latest tip, nothing to update.")
        return

    print(f"pin-update-helper: {len(changed)} repo(s) have new commits:")
    for repo, (old, new) in sorted(changed.items()):
        print(f"  {repo}: {old} -> {new}")

    if dry:
        print("(dry run: kas/pins.yml NOT written)")
    else:
        PINS_FILE.write_text(pins_text)
        print(f"pin-update-helper: wrote {PINS_FILE.relative_to(cs.REPO_ROOT)} - review the diff, then commit.")


if __name__ == "__main__":
    main()
