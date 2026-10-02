#!/usr/bin/env bash
# SPDX-License-Identifier: MIT
# aglsetup.sh-muscle-memory wrapper around `kas-container shell`, for local exploration only -
# `make build`/`make validate` (via ci/scripts/) remain the curated, matrix-backed, CI path.
#
#   scripts/aglsetup.sh -m qemux86-64 agl-demo agl-devel
#
# Unlike `make build/validate/shell`, this never checks ci/build-matrix.yaml - any machine with
# a kas/machine/<name>.yml and any features each with a kas/feature/<name>.yml work, in any
# combination, no curated entry required (ci/scripts/_compose_kasfiles.py --no-matrix). Feature
# dependencies (classic aglsetup.sh's included.dep, e.g. agl-demo pulling in agl-pipewire) are
# resolved by kas itself via each fragment's own header.includes: - nothing to do here.
#
# Unlike the real aglsetup.sh (which sources into and mutates the CURRENT shell's env via
# oe-init-build-env, never spawning a subshell), this execs into an interactive
# `kas-container shell` - the kas-world equivalent of "you now have a configured, ready-to-build
# environment": run `bitbake <target>` once inside, `exit`/Ctrl-D to leave.
set -euo pipefail

# readlink -f (not just dirname "${BASH_SOURCE[0]}") because this script is also reached via
# the meta-agl/scripts/aglsetup.sh symlink - resolving the symlink first is required to land on
# this file's real directory instead of the symlink's.
REPO_ROOT="$(cd "$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")/.." && pwd)"
cd "$REPO_ROOT"
# shellcheck source=../ci/scripts/_kas_runtime_args.sh
source ci/scripts/_kas_runtime_args.sh

usage() {
  cat <<EOF
Usage: $(basename "$0") [-m|--machine MACHINE] [-b|--builddir DIR] [feature [feature ...]]

Drops you into an interactive kas-container shell for MACHINE plus the given features (no
ci/build-matrix.yaml entry required - any combination of what's listed below works). Run
bitbake yourself once inside, e.g. \`bitbake agl-image-minimal\`.

  -m, --machine MACHINE   default: qemux86-64
  -b, --builddir DIR      use DIR as the kas build directory instead of the shared build/
                          (maps to kas-container's own KAS_BUILD_DIR; created if missing)
  -h, --help              this message

Available machines:
$(for f in kas/machine/*.yml; do b="$(basename "$f" .yml)"; printf '  %s\n' "$b"; done)

Available features:
$(for f in kas/feature/*.yml; do b="$(basename "$f" .yml)"; printf '  %s\n' "$b"; done)
EOF
}

MACHINE="qemux86-64"
BUILDDIR=""
FEATURES=()
while [ $# -gt 0 ]; do
  case "$1" in
    -m|--machine) MACHINE="$2"; shift 2 ;;
    -b|--builddir) BUILDDIR="$2"; shift 2 ;;
    -h|--help) usage; exit 0 ;;
    --) shift; FEATURES+=("$@"); break ;;
    -*) echo "$(basename "$0"): unknown option: $1" >&2; usage >&2; exit 2 ;;
    *) FEATURES+=("$1"); shift ;;
  esac
done

FEATURES_CSV="$(IFS=,; echo "${FEATURES[*]:-}")"

run_machine_setup_hooks "$MACHINE"

KASFILES="$(python3 ci/scripts/_compose_kasfiles.py --machine "$MACHINE" --features "$FEATURES_CSV" --no-matrix)$(kas_extra_includes)"

if [ -n "$BUILDDIR" ]; then
  export KAS_BUILD_DIR="$BUILDDIR"
fi

echo "aglsetup.sh: kas shell $KASFILES"
echo "aglsetup.sh: common targets once inside: agl-image-boot, agl-image-minimal," \
     "agl-image-weston, agl-image-compositor"
# No ci/build-matrix.yaml entry to resolve a target from (matrix-free, see above) - default
# KAS_TARGET to the same image Makefile's own TARGET default builds, just so kas's own
# "To start the default build, run: ..." suggestion names an AGL image instead of oe-core's
# generic core-image-minimal. A hint only: pick any bitbake target you like once inside.
# shellcheck disable=SC2046
KAS_TARGET="${KAS_TARGET:-agl-image-minimal}" KAS_WORK_DIR="$REPO_ROOT" \
  exec kas-container $(kas_runtime_args) shell "$KASFILES"
