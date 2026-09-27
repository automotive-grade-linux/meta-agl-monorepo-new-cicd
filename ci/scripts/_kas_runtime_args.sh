# SPDX-License-Identifier: MIT
# Sourced by setup.sh/build.sh/validate.sh (and the Makefile's shell/lock targets). Three
# things live here:
#
# 1. parse_common_args() - the --machine/--features/--target/--extra-features/--sdk-allowed
#    CLI parsing shared verbatim by all three scripts (was duplicated 3x before, see WIP.md
#    "round 11").
# 2. matrix_fields() - the "look up one or more ci/build-matrix.yaml fields for the current
#    MACHINE/FEATURES/MATRIX_TARGET/EXTRA_FEATURES" call, shared by all three scripts (each
#    wants a different field set: setup.sh needs kasfiles+eula, build.sh needs
#    kasfiles+target+sdk, validate.sh needs kasfiles+target).
# 3. kas_extra_includes()/kas_ci_includes()/kas_runtime_args() - support two optional,
#    independent local-developer conveniences, never used in CI unless these env vars are
#    explicitly set there too:
#
#   AGL_SSTATE_DIR   - a persistent SSTATE_DIR shared across checkouts/machines/features,
#                       e.g. `export AGL_SSTATE_DIR=$HOME/.yocto/sstate-cache`. Bind-mounted
#                       into the container at the same path and wired into kas via the
#                       ci/kas/local/sstate-shared.yml fragment. Left unset, SSTATE_DIR
#                       falls back to oe-core's own default (build/sstate-cache, inside the
#                       repo work dir) - which is exactly what the GitHub Actions/GitLab CI
#                       cache configs already target, so CI needs no changes.
#   AGL_SITE_CONF    - path to a personal site.conf, e.g. `export AGL_SITE_CONF=$HOME/.yocto/site.conf`.
#                       bitbake auto-includes conf/site.conf from the build dir with no kas
#                       config changes needed - this just bind-mounts the file into place
#                       (read-only) at /work/build/conf/site.conf, kas-container's fixed
#                       mount point for the repo/work dir.
#
# Both can be set together; if your site.conf also sets SSTATE_DIR, keep the two in sync
# yourself (this script mounts the directory named by AGL_SSTATE_DIR verbatim, it doesn't
# parse site.conf to discover it).

# Sets MACHINE/FEATURES/MATRIX_TARGET/EXTRA_FEATURES/SDK_ALLOWED as globals from "$@". Example:
#   MACHINE="" FEATURES="" MATRIX_TARGET="" EXTRA_FEATURES="" SDK_ALLOWED=""
#   parse_common_args --machine qemux86-64 --features agl-demo --target agl-ivi-demo-flutter
#   echo "$MACHINE $FEATURES $MATRIX_TARGET"   # -> qemux86-64 agl-demo agl-ivi-demo-flutter
# --sdk-allowed is only read by build.sh; setup.sh/validate.sh just never look at $SDK_ALLOWED.
# --target sets MATRIX_TARGET, not TARGET, because build.sh separately computes the actual
# resolved bitbake target name (via `_compose_kasfiles.py --fields target`) into a variable
# called TARGET - two different things, kept apart on purpose.
parse_common_args() {
  MACHINE=""
  FEATURES=""
  MATRIX_TARGET=""      # disambiguates when several images share machine+features
  EXTRA_FEATURES=""     # local-only extras layered on top of a matched entry, e.g. agl-devel
  SDK_ALLOWED="false"
  while [ $# -gt 0 ]; do
    case "$1" in
      --machine) MACHINE="$2"; shift 2 ;;
      --features) FEATURES="$2"; shift 2 ;;
      --target) MATRIX_TARGET="$2"; shift 2 ;;
      --extra-features) EXTRA_FEATURES="$2"; shift 2 ;;
      --sdk-allowed) SDK_ALLOWED="$2"; shift 2 ;;
      *) echo "$(basename "$0"): unknown argument: $1" >&2; exit 2 ;;
    esac
  done
  [ -n "$MACHINE" ] || { echo "$(basename "$0"): --machine is required" >&2; exit 2; }
}

# Looks up "$1" (comma-separated ci/scripts/_compose_kasfiles.py --fields names, e.g.
# "kasfiles,target,sdk") against MACHINE/FEATURES/MATRIX_TARGET/EXTRA_FEATURES, one matrix
# lookup instead of one call per field, and fills the _MATRIX_FIELDS array (index 0 = first
# requested field, etc). Uses a plain variable + here-string (not `mapfile -t arr < <(cmd)`
# directly) so a lookup failure (e.g. no matching matrix entry) still triggers the caller's
# `set -e`, which process substitution's exit status would not.
matrix_fields() {
  local _raw
  _raw="$(python3 ci/scripts/_compose_kasfiles.py --machine "$MACHINE" --features "$FEATURES" --target "$MATRIX_TARGET" --extra-features "$EXTRA_FEATURES" --fields "$1")"
  mapfile -t _MATRIX_FIELDS <<< "$_raw"
}

kas_extra_includes() {
  if [ -n "${AGL_SSTATE_DIR:-}" ]; then
    printf ':ci/kas/local/sstate-shared.yml'
  fi
}

# ci/kas/ci-only.yml carries agl-devel (passwordless login, needed for hardware-in-the-loop
# testing) plus AGL's agl-ci build tuning. Colon-joined whenever CI=true - never part of
# ci/build-matrix.yaml's features: column, never part of a plain local build.
kas_ci_includes() {
  if [ "${CI:-}" = "true" ]; then
    printf ':ci/kas/ci-only.yml'
  fi
}

kas_runtime_args() {
  # kas-container's own arg parser expects `--runtime-args VALUE` as two separate words
  # (space-separated argv entries - confirmed from its source: `case "$1" in
  # --runtime-args|--docker-args) ... "$2"; shift 2`), NOT `--runtime-args=VALUE`. Each
  # VALUE here must itself be one word (no spaces) since callers expand
  # $(kas_runtime_args) unquoted - an embedded space inside VALUE would get word-split
  # too, breaking the `-v host:container` pairing into a 3rd/4th stray argv entry.

  # kas-container's own script only adds `--security-opt label=disable` for the podman
  # engine (see its `case "${KAS_CONTAINER_ENGINE}"` block) - never for docker, even when
  # the Docker daemon itself has SELinux enabled (`"selinux-enabled": true` in
  # /etc/docker/daemon.json - a real, seen-in-practice workstation config, not hypothetical,
  # see WIP.md "round 12"). Without it, SELinux denies the container's bind-mounted access
  # to /work at the kernel level, surfacing as a plain `PermissionError: [Errno 13]
  # Permission denied: '/work/build'` from kas - Unix owner/group/mode all look fine, so
  # this is easy to misdiagnose as a UID mismatch. Harmless no-op everywhere else (plain
  # Docker without SELinux ignores it; podman already gets it from kas-container itself, so
  # this is just a redundant duplicate there, not a conflict) - always emitted.
  printf -- '--runtime-args --security-opt --runtime-args label=disable '

  if [ -n "${AGL_SSTATE_DIR:-}" ]; then
    mkdir -p "${AGL_SSTATE_DIR}"
    # kas-container only forwards a fixed explicit set of env vars into the container
    # (KAS_WORK_DIR, USER_ID, TERM, ...) - AGL_SSTATE_DIR itself needs -e too, or
    # ${AGL_SSTATE_DIR} stays unexpanded in local.conf despite kas's `env:` passthrough
    # declaration (that only affects BB_ENV_PASSTHROUGH_ADDITIONS for task shells, not
    # whether the value reaches the container process at all).
    printf -- '--runtime-args -v --runtime-args %s:%s --runtime-args -e --runtime-args AGL_SSTATE_DIR=%s ' \
      "${AGL_SSTATE_DIR}" "${AGL_SSTATE_DIR}" "${AGL_SSTATE_DIR}"
  fi
  if [ -n "${AGL_SITE_CONF:-}" ]; then
    printf -- '--runtime-args -v --runtime-args %s:/work/build/conf/site.conf:ro ' "${AGL_SITE_CONF}"
  fi
}
