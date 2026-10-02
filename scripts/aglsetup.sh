#!/usr/bin/env bash
# SPDX-License-Identifier: MIT
# aglsetup.sh-muscle-memory wrapper around bitbake-setup, for local exploration only -
# `make build`/`make validate` (via ci/scripts/) remain the curated, matrix-backed, CI path.
#
#   scripts/aglsetup.sh -m qemux86-64 agl-demo agl-devel
#
# Unlike `make build/validate/shell`, this never checks ci/build-matrix.yaml - any machine with
# a kas/machine/<name>.yml and any features each with a kas/feature/<name>.yml work, in any
# combination, no curated entry required (ci/scripts/_compose_setup.py --no-matrix). Feature
# dependencies (classic aglsetup.sh's included.dep, e.g. agl-demo pulling in agl-pipewire) are
# resolved by _compose_setup.py via each fragment's own header.includes: - nothing to do here.
#
# Unlike the real aglsetup.sh (which sources into and mutates the CURRENT shell's env via
# oe-init-build-env, never spawning a subshell), this creates/refreshes the bitbake-setup
# setup build/<machine>[-<feature>...] and then opens an interactive shell for it in the
# agl-ci-builder container (build/init-build-env already sourced) - the equivalent of "you
# now have a configured, ready-to-build environment": run `bitbake <target>` once inside,
# `exit`/Ctrl-D to leave. Re-running with the same machine/features re-syncs the existing
# setup (bitbake-setup update) instead of starting over.
set -euo pipefail

# readlink -f (not just dirname "${BASH_SOURCE[0]}") because this script is also reached via
# the meta-agl/scripts/aglsetup.sh symlink - resolving the symlink first is required to land on
# this file's real directory instead of the symlink's.
REPO_ROOT="$(cd "$(dirname "$(readlink -f "${BASH_SOURCE[0]}")")/.." && pwd)"
cd "$REPO_ROOT"
# shellcheck source=../ci/scripts/_common.sh
source ci/scripts/_common.sh

usage() {
  cat <<EOF
Usage: $(basename "$0") [-m|--machine MACHINE] [-b|--builddir DIR] [feature [feature ...]]

Creates (or re-syncs) a bitbake-setup setup for MACHINE plus the given features and drops you
into an interactive container shell in it (no ci/build-matrix.yaml entry required - any
combination of what's listed below works). Run bitbake yourself once inside, e.g.
\`bitbake agl-image-minimal\`. Env: AGL_CONTAINER_IMAGE, AGL_SSTATE_DIR, AGL_SITE_CONF,
AGL_FLOATING, CI (see docs/setup.md).

  -m, --machine MACHINE   default: qemux86-64
  -b, --builddir DIR      use DIR as the bitbake-setup top directory instead of the shared
                          build/ (must be inside this repo checkout; created if missing)
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

if [ -n "$BUILDDIR" ]; then
  # The repo is the only thing mounted into the container, so the build dir must live in it.
  AGL_BUILD_DIR="$(realpath -m --relative-to="$REPO_ROOT" "$BUILDDIR")"
  case "$AGL_BUILD_DIR" in
    ..|../*|/*) echo "$(basename "$0"): --builddir must be inside $REPO_ROOT" >&2; exit 2 ;;
  esac
  export AGL_BUILD_DIR
  # shellcheck source=../ci/scripts/_common.sh
  source ci/scripts/_common.sh   # re-derive BUILD_TOP & co.
fi

mapfile -t _F < <(python3 ci/scripts/_compose_setup.py --machine "$MACHINE" --features "$FEATURES_CSV" \
                    --no-matrix --work-dir "$WORK_DIR" --fields config,setup-name)
echo "aglsetup.sh: bitbake-setup ${_F[1]} (${_F[0]})"
echo "aglsetup.sh: common targets once inside: agl-image-boot, agl-image-minimal," \
     "agl-image-weston, agl-image-compositor"
bbsetup_sync "${_F[0]}" "${_F[1]}"
bbsetup_shell "${_F[1]}"
