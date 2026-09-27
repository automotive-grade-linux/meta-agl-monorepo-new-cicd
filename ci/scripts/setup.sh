#!/usr/bin/env bash
# SPDX-License-Identifier: MIT
# Checkout kas repos for a (machine, features) combination. Runs identically in CI (via
# kas-container) and locally for developers (via `make setup`).
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$REPO_ROOT"
# shellcheck source=./_kas_runtime_args.sh
source ci/scripts/_kas_runtime_args.sh

parse_common_args "$@"

matrix_fields kasfiles,eula
KASFILES="${_MATRIX_FIELDS[0]}$(kas_extra_includes)$(kas_ci_includes)"

# EULA (decision: CI auto-accepts, local dev keeps the interactive prompt unless it
# already set the env var itself).
if [ "${_MATRIX_FIELDS[1]}" = "True" ] \
   && [ "${CI:-}" = "true" ]; then
  MACHINE_UPPER="$(echo "$MACHINE" | tr 'a-z-' 'A-Z_')"
  export "EULA_${MACHINE_UPPER}=1"
  echo "setup.sh: CI run, auto-accepting EULA via EULA_${MACHINE_UPPER}=1"
fi

# h3ulcb/m3ulcb proprietary R-Car gfx/multimedia package copy (aglsetup.sh's 50_setup.sh
# equivalent - imperative, no kas mechanism for this). Applies to h3ulcb, h3ulcb-kf,
# m3ulcb, m3ulcb-kf only, not the -nogfx variants. CI never has the proprietary zips
# (no credentials/redistribution rights - see docs/setup.md) and never builds these 4
# machines (ci/build-matrix.yaml gives them tiers: []); this only matters for local
# `make build MACHINE=h3ulcb` etc. runs.
case "$MACHINE" in
  h3ulcb|h3ulcb-kf|m3ulcb|m3ulcb-kf)
    HOOK="layers/meta-agl/meta-agl-bsp/meta-rcar-gen3/scripts/setup_mm_packages.sh"
    if [ -f "$HOOK" ]; then
      echo "setup.sh: running proprietary R-Car package hook for $MACHINE"
      # setup_mm_packages.sh only defines copy_mm_packages(), the caller must source it
      # and invoke the function (matches meta-agl/templates/machine/h3ulcb/50_setup.sh).
      # Subshell: the function cd's around and we don't want that to affect setup.sh.
      (
        export METADIR="$REPO_ROOT/layers"
        # shellcheck source=/dev/null
        source "$HOOK"
        copy_mm_packages
      ) || echo "setup.sh: WARNING: proprietary package setup failed/incomplete for" \
                "$MACHINE - see docs/setup.md for what's required on your workstation" >&2
    else
      echo "setup.sh: WARNING: $HOOK not found, skipping proprietary package setup for $MACHINE" >&2
    fi
    ;;
esac

echo "setup.sh: kas checkout $KASFILES"
# shellcheck disable=SC2046
KAS_WORK_DIR="$REPO_ROOT" kas-container $(kas_runtime_args) checkout "$KASFILES"
