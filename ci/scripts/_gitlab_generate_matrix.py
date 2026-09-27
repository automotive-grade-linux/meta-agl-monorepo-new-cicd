#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Generates a GitLab child-pipeline YAML with parallel:matrix: already filled in for the
given tier, printed to stdout. GitLab's parallel:matrix: needs static YAML - it can't consume
a dynamic list computed in an earlier job the way GitHub Actions' fromJson(needs.x.outputs.y)
can, so ci/gitlab/read-matrix.yml's generate-matrix job runs this and passes the result to
ci/gitlab/build.yml's trigger-build job as `trigger: include: artifact:`.

Usage: python3 ci/scripts/_gitlab_generate_matrix.py <tier> > matrix-pipeline.yml
"""
import sys
from pathlib import Path

import yaml

sys.path.insert(0, str(Path(__file__).resolve().parent))
from _matrix import expand_entries  # noqa: E402


def main():
    tier = sys.argv[1]
    sdk_allowed = "true" if tier == "release" else "false"
    entries = [e for e in expand_entries() if tier in e.get("tiers", [])]

    pipeline = {
        "build-entry": {
            "image": "${AGL_CI_IMAGE}",
            "variables": {"CI": "true"},
            "script": [
                "make validate MACHINE=$MACHINE FEATURES=\"$FEATURES\" TARGET=\"$TARGET\"",
                f"make build MACHINE=$MACHINE FEATURES=\"$FEATURES\" TARGET=\"$TARGET\" SDK_ALLOWED={sdk_allowed}",
            ],
            "cache": [
                {"key": "sstate-$MACHINE", "paths": ["build/sstate-cache"]},
                {"key": "downloads", "paths": ["build/downloads"]},
            ],
            "artifacts": {
                "when": "always",
                "paths": ["build/artifacts/", "build/validate-results/"],
                "expire_in": "7 days" if tier == "push-pr" else ("30 days" if tier == "nightly" else "90 days"),
            },
            "parallel": {
                "matrix": [
                    {"MACHINE": e["machine"], "FEATURES": ",".join(e.get("features", [])),
                     "TARGET": e["target"]}
                    for e in entries
                ]
            },
        }
    }
    yaml.safe_dump(pipeline, sys.stdout, default_flow_style=False)


if __name__ == "__main__":
    main()
