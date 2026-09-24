#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Shared ci/build-matrix.yaml loader/expander, used by ci/scripts/_compose_kasfiles.py, the
GitHub Actions read-matrix job, and ci/gitlab/read-matrix.yml - so the images:/machines:
expansion logic (and the incompatible-feature check) exists exactly once.

ci/build-matrix.yaml schema:
  machine_eula: {<machine>: true}   - machines needing EULA_<MACHINE>=1, same for every image.
  images:
    - target: <bitbake target>
      features: [<aglsetup-style feature name>, ...]
      sdk: bool
      tiers: [...]                  - default tiers for every machine below
      machines:
        - <machine name>                          - uses the image's default tiers
        - <machine name>: {tiers: [<override>]}   - per-machine tier override

expand_entries() flattens this into the historical one-row-per-(machine,features,target) shape
(dicts with machine/features/target/sdk/eula/tiers keys) that every consumer actually wants to
filter/lookup against.

Run directly (`python3 ci/scripts/_matrix.py`) to validate ci/build-matrix.yaml: checks for
INCOMPATIBLE_FEATURES violations and duplicate (machine, features, target) rows. Also run by
`make validate` / CI.
"""
import sys
from pathlib import Path

import yaml

REPO_ROOT = Path(__file__).resolve().parents[2]
MATRIX_FILE = REPO_ROOT / "ci" / "build-matrix.yaml"

# Feature pairs that must never appear together in one images: entry. aglsetup.sh itself never
# checked this (purely additive), so this repo enforces it instead.
INCOMPATIBLE_FEATURES = [
    {"agl-kvm", "agl-xen"},  # two hypervisor backends, can't coexist on one image
]


def load_matrix():
    return yaml.safe_load(MATRIX_FILE.read_text())


def check_incompatible(features):
    """Returns the offending feature names if `features` contains two mutually-incompatible
    features, else None."""
    fset = set(features)
    for group in INCOMPATIBLE_FEATURES:
        hit = fset & group
        if len(hit) > 1:
            return hit
    return None


def expand_entries(data=None):
    data = data if data is not None else load_matrix()
    machine_eula = data.get("machine_eula", {})
    entries = []
    for img in data.get("images", []):
        bad = check_incompatible(img["features"])
        if bad:
            raise ValueError(
                f"ci/build-matrix.yaml: target {img['target']!r} combines mutually-incompatible "
                f"features {sorted(bad)} - see INCOMPATIBLE_FEATURES in ci/scripts/_matrix.py"
            )
        for m in img["machines"]:
            if isinstance(m, str):
                name, overrides = m, {}
            else:
                (name, overrides), = m.items()
            entries.append({
                "machine": name,
                "features": img["features"],
                "target": img["target"],
                "sdk": img["sdk"],
                "eula": machine_eula.get(name, False),
                "tiers": overrides.get("tiers", img["tiers"]),
            })
    return entries


def main():
    try:
        entries = expand_entries()
    except ValueError as exc:
        print(f"error: {exc}", file=sys.stderr)
        sys.exit(1)

    seen = set()
    dupes = 0
    for e in entries:
        key = (e["machine"], tuple(sorted(e["features"])), e["target"])
        if key in seen:
            print(f"error: duplicate matrix entry for {key}", file=sys.stderr)
            dupes += 1
        seen.add(key)

    print(f"ci/build-matrix.yaml: {len(entries)} expanded entries, {dupes} duplicate(s)")
    sys.exit(1 if dupes else 0)


if __name__ == "__main__":
    main()
