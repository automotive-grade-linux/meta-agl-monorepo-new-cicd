#!/usr/bin/env bash
# SPDX-License-Identifier: MIT
# Build a matrix entry's target image, optionally populate_sdk (gated by both the
# entry's sdk flag and the caller-resolved --sdk-allowed), and copy deploy artifacts.
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$REPO_ROOT"
# shellcheck source=./_common.sh
source ci/scripts/_common.sh

parse_common_args "$@"

matrix_fields setup-name,target,sdk
SETUP_NAME="${_MATRIX_FIELDS[0]}"
TARGET="${_MATRIX_FIELDS[1]}"
ENTRY_SDK="${_MATRIX_FIELDS[2]}"

echo "build.sh: bitbake $TARGET in setup $SETUP_NAME"
bbsetup_run "$SETUP_NAME" "bitbake $TARGET"

if [ "$ENTRY_SDK" = "True" ] && [ "$SDK_ALLOWED" = "true" ]; then
  echo "build.sh: populating SDK for $TARGET"
  bbsetup_run "$SETUP_NAME" "bitbake -c populate_sdk $TARGET"
fi

ARTIFACT_DIR="${ARTIFACT_DIR:-$REPO_ROOT/build/artifacts/$MACHINE-$TARGET}"
mkdir -p "$ARTIFACT_DIR"
DEPLOY_DIR="$REPO_ROOT/build/$SETUP_NAME/build/tmp/deploy"
[ -d "$DEPLOY_DIR/images/$MACHINE" ] && cp -a "$DEPLOY_DIR/images/$MACHINE/." "$ARTIFACT_DIR/" || true
[ -d "$DEPLOY_DIR/sdk" ] && cp -a "$DEPLOY_DIR/sdk/." "$ARTIFACT_DIR/sdk/" || true
echo "build.sh: artifacts in $ARTIFACT_DIR"
