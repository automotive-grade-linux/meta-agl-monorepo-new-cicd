#!/usr/bin/env bash
# SPDX-License-Identifier: MIT
# Create/refresh the bitbake-setup setup (layer checkout + build/conf) for a (machine,
# features) combination. Runs identically in CI and locally for developers (`make setup`).
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$REPO_ROOT"
# shellcheck source=./_common.sh
source ci/scripts/_common.sh

parse_common_args "$@"

matrix_fields config,setup-name
CONFIG="${_MATRIX_FIELDS[0]}"
SETUP_NAME="${_MATRIX_FIELDS[1]}"

run_machine_setup_hooks "$MACHINE"

echo "setup.sh: bitbake-setup $SETUP_NAME ($CONFIG)"
bbsetup_sync "$CONFIG" "$SETUP_NAME"
