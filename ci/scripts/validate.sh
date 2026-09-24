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

KASFILES="$(python3 ci/scripts/_compose_kasfiles.py --machine "$MACHINE" --features "$FEATURES" --target "$MATRIX_TARGET" --extra-features "$EXTRA_FEATURES")$(kas_extra_includes)$(kas_ci_includes)"
RESULTS_DIR="${RESULTS_DIR:-$REPO_ROOT/build/validate-results}"
mkdir -p "$RESULTS_DIR"

status=0
run_check() {
  local name="$1" kasfiles="$2"; shift 2
  echo "validate.sh: running $name"
  # shellcheck disable=SC2046
  if kas-container $(kas_runtime_args) shell "$kasfiles" -c "$*" >"$RESULTS_DIR/$name.log" 2>&1; then
    echo "validate.sh: $name PASSED"
  else
    echo "validate.sh: $name FAILED (see $RESULTS_DIR/$name.log)"
    status=1
  fi
}

# Host-side check, no container needed: catches build-matrix.yaml authoring mistakes
# (INCOMPATIBLE_FEATURES violations, duplicate (machine, features, target) rows) before
# spending any container/bitbake time.
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

run_host_check "matrix-validate" python3 ci/scripts/_matrix.py

run_check "bitbake-parse" "$KASFILES" "bitbake -p"

# yocto-check-layer needs actual bitbake layer roots (dirs with their own conf/layer.conf),
# not the meta-agl/meta-agl-demo/meta-agl-devel container repo roots - those hold many
# sublayers (meta-agl-core, meta-pipewire, etc.) but aren't layers themselves. Paths are
# hardcoded to /work (kas-container's fixed mount point for the repo root - $KAS_WORK_DIR
# is NOT propagated into the bitbake-sourced shell env, confirmed empty there) since
# `kas shell -c` runs from /work/build, not the repo root. Runs against the bare
# _validate-base combo (no AGL layers pre-loaded) since yocto-check-layer manages its own
# layer-under-test additions and errors on duplicate BBFILE_COLLECTIONS otherwise.
run_check "yocto-check-layer" "ci/kas/_validate-base.yml:ci/kas/pins.yml" \
  'yocto-check-layer $(find /work/layers/meta-agl /work/layers/meta-agl-demo /work/layers/meta-agl-devel -maxdepth 3 -name layer.conf -path "*/conf/*" | sed "s#/conf/layer.conf##")'

run_check "license-manifest" "$KASFILES" \
  "bitbake -e $(python3 ci/scripts/_compose_kasfiles.py --machine "$MACHINE" --features "$FEATURES" --target "$MATRIX_TARGET" --field target) | grep -E '^LICENSE='"

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
