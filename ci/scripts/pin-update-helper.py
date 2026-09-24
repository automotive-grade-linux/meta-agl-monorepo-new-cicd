#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Takes a kas-generated lockfile (resolved against ci/kas/floating.yml - i.e. every repo's
current branch-tip commit, see `make pin-update`) and updates ci/kas/pins.yml in place with
whatever changed, via targeted text substitution (keeps pins.yml's existing comments/grouping
intact - no full YAML round-trip, which would lose them). Prints a summary of what changed.

Usage: ci/scripts/pin-update-helper.py <resolved-lock-file> [--dry-run]
"""
import argparse
import re
import sys
from pathlib import Path

import yaml

REPO_ROOT = Path(__file__).resolve().parents[2]
PINS_FILE = REPO_ROOT / "ci" / "kas" / "pins.yml"


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("lockfile", help="kas-generated lockfile with the freshly resolved commits")
    ap.add_argument("--dry-run", action="store_true", help="show what would change, don't write pins.yml")
    args = ap.parse_args()

    resolved = yaml.safe_load(Path(args.lockfile).read_text())
    new_commits = {
        repo: info["commit"]
        for repo, info in resolved.get("overrides", {}).get("repos", {}).items()
        if "commit" in info
    }

    pins_text = PINS_FILE.read_text()
    changed = {}
    unknown = []
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
        print(
            f"pin-update-helper: {len(unknown)} repo(s) resolved but not found in "
            f"{PINS_FILE.relative_to(REPO_ROOT)} (add them there first if they're meant to be "
            f"pinned): {', '.join(sorted(unknown))}",
            file=sys.stderr,
        )

    if not changed:
        print("pin-update-helper: everything already at the latest tip, nothing to update.")
        return

    print(f"pin-update-helper: {len(changed)} repo(s) have new commits:")
    for repo, (old, new) in sorted(changed.items()):
        print(f"  {repo}: {old} -> {new}")

    if args.dry_run:
        print("(--dry-run: ci/kas/pins.yml NOT written)")
    else:
        PINS_FILE.write_text(pins_text)
        print(f"pin-update-helper: wrote {PINS_FILE.relative_to(REPO_ROOT)} - review the diff, then commit.")


if __name__ == "__main__":
    main()
