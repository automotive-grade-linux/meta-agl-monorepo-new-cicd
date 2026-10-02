#!/usr/bin/env bash
# SPDX-License-Identifier: MIT
# Interactive shell inside a matrix entry's bitbake-setup setup (`make shell`).
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$REPO_ROOT"
# shellcheck source=./_common.sh
source ci/scripts/_common.sh

parse_common_args "$@"

matrix_fields setup-name
bbsetup_shell "${_MATRIX_FIELDS[0]}"
