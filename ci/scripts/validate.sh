#!/usr/bin/env bash
# SPDX-License-Identifier: MIT
# Full layer QA suite: bitbake world-parse, yocto-check-layer, license/patch checks, lint.
# Each sub-check logs separately to $RESULTS_DIR for the PR/MR summary step.
set -uo pipefail  # not -e: run every check, collect all results, report at the end

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "$REPO_ROOT"
# shellcheck source=./_kas_runtime_args.sh
source ci/scripts/_kas_runtime_args.sh

parse_common_args "$@"

matrix_fields kasfiles,target
KASFILES="${_MATRIX_FIELDS[0]}$(kas_extra_includes)$(kas_ci_includes)"
MATRIX_ENTRY_TARGET="${_MATRIX_FIELDS[1]}"
RESULTS_DIR="${RESULTS_DIR:-$REPO_ROOT/build/validate-results}"
mkdir -p "$RESULTS_DIR"

status=0
# Runs "$@" as the check named $1, logs to $RESULTS_DIR/$name.log, and records PASSED/FAILED.
run_host_check() {
  local name="$1"; shift
  echo "validate.sh: running $name"
  if "$@" >"$RESULTS_DIR/$name.log" 2>&1; then
    echo "validate.sh: $name PASSED"
  else
    echo "validate.sh: $name FAILED (see $RESULTS_DIR/$name.log)"
    status=1
  fi
}

# In-container check: same PASSED/FAILED bookkeeping as run_host_check, just run inside
# kas-container instead of directly on the host.
run_check() {
  local name="$1" kasfiles="$2"; shift 2
  # shellcheck disable=SC2046
  run_host_check "$name" kas-container $(kas_runtime_args) shell "$kasfiles" -c "$*"
}

run_host_check "matrix-validate" python3 ci/scripts/_matrix.py

run_check "bitbake-parse" "$KASFILES" "bitbake -p"

# yocto-check-layer needs actual bitbake layer roots (dirs with their own conf/layer.conf) -
# ci/build-matrix.yaml's check_layers.layers names them exactly, so CHECK_LAYERS entries are
# used directly, no more searching for them. Paths are hardcoded to /work (kas-container's
# fixed mount point for the repo root - $KAS_WORK_DIR is NOT propagated into the
# bitbake-sourced shell env, confirmed empty there) since `kas shell -c` runs from
# /work/build, not the repo root. Runs against the bare _validate-base combo (no AGL layers
# pre-loaded) since yocto-check-layer manages its own layer-under-test additions and errors
# on duplicate BBFILE_COLLECTIONS otherwise.
#
# CHECK_LAYERS (space-separated paths matching ci/build-matrix.yaml's check_layers.layers;
# set via `make validate CHECK_LAYERS="..."` - see Makefile) defaults to just meta-agl-core:
# checking every vendored sublayer is slow and most of them aren't what's actively being
# changed here. `CHECK_LAYERS=all` checks every curated layer. Each layer's dependencies
# (yocto-check-layer's --dependency/--additional-layers) come from
# ci/build-matrix.yaml's check_layers: key via _matrix.py - add a newly-vendored layer's
# dependency info there, not here.
CHECK_LAYERS="${CHECK_LAYERS:-layers/meta-agl/meta-agl-core}"
if [ "$CHECK_LAYERS" = "all" ]; then
  CHECK_LAYERS="$(python3 ci/scripts/_matrix.py --list-check-layers | tr '\n' ' ')"
fi
# shellcheck disable=SC2086
CHECK_LAYER_ARGS="$(python3 ci/scripts/_matrix.py --check-layer-args $CHECK_LAYERS)"
CHECK_LAYER_PATHS=""
for l in $CHECK_LAYERS; do
  CHECK_LAYER_PATHS="$CHECK_LAYER_PATHS /work/$l"
done
run_check "yocto-check-layer" "ci/kas/_validate-base.yml:ci/kas/pins.yml" \
  "yocto-check-layer$CHECK_LAYER_PATHS $CHECK_LAYER_ARGS"

run_check "license-manifest" "$KASFILES" \
  "bitbake -e $MATRIX_ENTRY_TARGET | grep -E '^LICENSE='"

# patchtest needs the separate patchtest-oe metadata/results-parser wired up against a
# concrete PR diff range - not yet set up (TODO, see WIP.md deferred items). Skipped for
# now rather than faked as passing.
if [ "${SKIP_PATCHTEST:-1}" != "1" ]; then
  run_check "patchtest" "$KASFILES" "patchtest --repo layers/meta-agl --base-ref ${PATCHTEST_BASE_REF:-HEAD~20}"
fi

# /usr/local/bin (where pip --break-system-packages installs system-wide) is pruned from
# PATH once bitbake's build environment is sourced (only /usr/{,s}bin and /{,s}bin survive)
# - call oelint-adv by absolute path rather than relying on PATH resolution.
run_check "recipe-lint" "$KASFILES" '/usr/local/bin/oelint-adv /work/layers/meta-agl /work/layers/meta-agl-demo /work/layers/meta-agl-devel'

exit $status
