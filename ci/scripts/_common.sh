# SPDX-License-Identifier: MIT
# Sourced by setup.sh/build.sh/validate.sh/scripts/aglsetup.sh (and the Makefile's shell
# target). Five things live here:
#
# 1. parse_common_args() - the --machine/--features/--target/--extra-features/--sdk-allowed
#    CLI parsing shared verbatim by all three scripts.
# 2. matrix_fields() - the "look up one or more ci/build-matrix.yaml fields (plus the
#    generated bitbake-setup config) for the current MACHINE/FEATURES/MATRIX_TARGET/
#    EXTRA_FEATURES" call, shared by all three scripts.
# 3. run_machine_setup_hooks() - the machine-keyed EULA-accept and h3ulcb/m3ulcb proprietary
#    package hook, shared by setup.sh (matrix-backed) and scripts/aglsetup.sh (matrix-free).
# 4. agl_container() - runs a command inside the agl-ci-builder image with the repo mounted
#    at /work (what kas-container used to do): UID/GID mapping, SELinux label=disable, env
#    forwarding, optional AGL_SSTATE_DIR bind mount.
# 5. bbsetup_*() - bitbake-setup bootstrap/init/update, shell and command helpers. All paths
#    inside the container are /work/...; the generated configs (ci/scripts/_compose_setup.py)
#    are told that via --work-dir.
#
# Two optional, independent local-developer conveniences, never used in CI unless these env
# vars are explicitly set there too:
#
#   AGL_SSTATE_DIR   - a persistent SSTATE_DIR shared across checkouts/machines/features,
#                       e.g. `export AGL_SSTATE_DIR=$HOME/.yocto/sstate-cache`. Bind-mounted
#                       into the container at the same path and wired into bitbake via the
#                       kas/local/sstate-shared.yml fragment (-> agl-setup/sstate-shared).
#                       Left unset, SSTATE_DIR is build/sstate-cache (build/site.conf), which
#                       is exactly what the GitHub Actions/GitLab CI cache configs target.
#   AGL_SITE_CONF    - path to a personal site.conf, e.g. `export AGL_SITE_CONF=$HOME/.yocto/site.conf`.
#                       Its content is appended to the shared build/site.conf that
#                       bitbake-setup symlinks into every setup's conf/ dir.
#
# Both can be set together; if your site.conf also sets SSTATE_DIR, keep the two in sync
# yourself.
#
# Other knobs: AGL_CONTAINER_IMAGE (default agl-ci-builder:dev; KAS_CONTAINER_IMAGE is ignored),
# AGL_CONTAINER_ENGINE (docker|podman, default: docker if present), BITBAKE_SETUP (path to an
# already-installed bitbake-setup, skips the pinned-bitbake bootstrap - host path, only
# useful with AGL_NO_CONTAINER=1), AGL_NO_CONTAINER=1 (run on the host directly; /work is
# then the repo root itself).

# Sets MACHINE/FEATURES/MATRIX_TARGET/EXTRA_FEATURES/SDK_ALLOWED as globals from "$@". Example:
#   MACHINE="" FEATURES="" MATRIX_TARGET="" EXTRA_FEATURES="" SDK_ALLOWED=""
#   parse_common_args --machine qemux86-64 --features agl-demo --target agl-ivi-demo-flutter
#   echo "$MACHINE $FEATURES $MATRIX_TARGET"   # -> qemux86-64 agl-demo agl-ivi-demo-flutter
# --sdk-allowed is only read by build.sh; setup.sh/validate.sh just never look at $SDK_ALLOWED.
# --target sets MATRIX_TARGET, not TARGET, because build.sh separately computes the actual
# resolved bitbake target name (via `_compose_setup.py --fields target`) into a variable
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

# Looks up "$1" (comma-separated ci/scripts/_compose_setup.py --fields names, e.g.
# "config,target,sdk") against MACHINE/FEATURES/MATRIX_TARGET/EXTRA_FEATURES, one matrix
# lookup instead of one call per field, and fills the _MATRIX_FIELDS array (index 0 = first
# requested field, etc). Uses a plain variable + here-string (not `mapfile -t arr < <(cmd)`
# directly) so a lookup failure (e.g. no matching matrix entry) still triggers the caller's
# `set -e`, which process substitution's exit status would not.
matrix_fields() {
  local _raw
  _raw="$(python3 ci/scripts/_compose_setup.py --machine "$MACHINE" --features "$FEATURES" --target "$MATRIX_TARGET" --extra-features "$EXTRA_FEATURES" --work-dir "$WORK_DIR" --fields "$1")"
  mapfile -t _MATRIX_FIELDS <<< "$_raw"
}

# Machine-keyed setup hooks shared by setup.sh and scripts/aglsetup.sh: EULA auto-accept
# (CI only) and the h3ulcb/m3ulcb proprietary R-Car package copy. Takes $1=machine. Assumes
# cwd is already the repo root (every caller cd's there before sourcing this file). Looks up
# EULA via `_matrix.py --machine-eula` (a flat machine-keyed table, independent of any
# (machine, features) matrix row) rather than matrix_fields(), so this works identically
# whether the caller is matrix-backed or matrix-free.
run_machine_setup_hooks() {
  local machine="$1" eula
  eula="$(python3 ci/scripts/_matrix.py --machine-eula "$machine")"

  # EULA (decision: CI auto-accepts, local dev keeps the interactive prompt unless it
  # already set the env var itself).
  if [ "$eula" = "True" ] && [ "${CI:-}" = "true" ]; then
    local machine_upper
    machine_upper="$(echo "$machine" | tr 'a-z-' 'A-Z_')"
    export "EULA_${machine_upper}=1"
    echo "$(basename "$0"): CI run, auto-accepting EULA via EULA_${machine_upper}=1"
  fi

  # h3ulcb/m3ulcb proprietary R-Car gfx/multimedia package copy (aglsetup.sh's 50_setup.sh
  # equivalent - imperative, no kas mechanism for this). Applies to h3ulcb, h3ulcb-kf,
  # m3ulcb, m3ulcb-kf only, not the -nogfx variants. CI never has the proprietary zips
  # (no credentials/redistribution rights - see docs/setup.md) and never builds these 4
  # machines (ci/build-matrix.yaml gives them tiers: []); this only matters for local use.
  case "$machine" in
    h3ulcb|h3ulcb-kf|m3ulcb|m3ulcb-kf)
      local hook="meta-agl/meta-agl-bsp/meta-rcar-gen3/scripts/setup_mm_packages.sh"
      if [ -f "$hook" ]; then
        echo "$(basename "$0"): running proprietary R-Car package hook for $machine"
        # setup_mm_packages.sh only defines copy_mm_packages(), the caller must source it
        # and invoke the function (matches meta-agl/templates/machine/h3ulcb/50_setup.sh).
        # Subshell: the function cd's around and we don't want that to affect the caller.
        (
          export METADIR="$PWD"
          # shellcheck source=/dev/null
          source "$hook"
          copy_mm_packages
        ) || echo "$(basename "$0"): WARNING: proprietary package setup failed/incomplete" \
                  "for $machine - see docs/setup.md for what's required on your workstation" >&2
      else
        echo "$(basename "$0"): WARNING: $hook not found, skipping proprietary package setup for $machine" >&2
      fi
      ;;
  esac
}


# Repo path as seen by bitbake-setup/bitbake: the container mount point, or the repo itself
# when running on the host (AGL_NO_CONTAINER=1). Callers set REPO_ROOT before sourcing us.
if [ "${AGL_NO_CONTAINER:-}" = "1" ]; then WORK_DIR="$REPO_ROOT"; else WORK_DIR=/work; fi
BUILD_REL="${AGL_BUILD_DIR:-build}"         # bitbake-setup top directory, relative to the repo
BUILD_TOP="$WORK_DIR/$BUILD_REL"
TOOL_DIR="$BUILD_TOP/.bitbake-setup-tool"   # pinned bitbake checkout providing bitbake-setup

# agl_container CMD [ARGS...] - run CMD inside the builder image, repo mounted at /work, as
# the calling host user (the image's entrypoint remaps `ci` to USER_ID/GROUP_ID, then drops
# privileges). Allocates a tty only when stdin is one, so it works in CI and interactively.
agl_container() {
  if [ "${AGL_NO_CONTAINER:-}" = "1" ]; then "$@"; return; fi
  local engine="${AGL_CONTAINER_ENGINE:-}"
  if [ -z "$engine" ]; then
    if command -v docker >/dev/null 2>&1; then engine=docker; else engine=podman; fi
  fi
  # Not KAS_CONTAINER_IMAGE: that variable (documented for the kas era) usually still points
  # at a kas-entrypoint image, which fails with "kas: error: argument cmd: invalid choice".
  local image="${AGL_CONTAINER_IMAGE:-agl-ci-builder:dev}"
  if [ -z "${_AGL_IMAGE_CHECKED:-}" ]; then
    # The image's entrypoint must exec the given command, not kas (stale pre-bitbake-setup image).
    if ! "$engine" run --rm --entrypoint grep "$image" -q 'exec "\$@"' /entrypoint.sh </dev/null 2>/dev/null; then
      echo "error: container image '$image' is missing or has the old kas entrypoint." >&2
      echo "       rebuild it: docker build -f ci/docker/Dockerfile -t $image ." >&2
      echo "       (set AGL_CONTAINER_IMAGE to use another tag; KAS_CONTAINER_IMAGE is ignored)" >&2
      return 1
    fi
    _AGL_IMAGE_CHECKED=1
  fi
  local -a args=(run --rm --init --log-driver=none --user=root
                 -v "$REPO_ROOT:/work:rw" --workdir /work
                 -e "USER_ID=$(id -u)" -e "GROUP_ID=$(id -g)"
                 # Docker daemons with "selinux-enabled": true deny the bind mount at the
                 # kernel level (surfaces as PermissionError on /work/build); harmless no-op
                 # without SELinux. Same reasoning the kas-container wrapper documented.
                 --security-opt label=disable)
  # kas-container pinned SHELL to /bin/bash for the same reason (bitbake's DEVSHELL derives from it).
  args+=(-e SHELL=/bin/bash)
  [ "$engine" = "podman" ] && args+=(--userns=keep-id)
  # tty when interactive; plain -i when stdin is a pipe, so `echo cmd | scripts/aglsetup.sh ...` works.
  if [ -t 0 ]; then args+=(-i -t); elif [ -p /dev/stdin ]; then args+=(-i); fi
  local var
  for var in TERM TZ NO_COLOR CI BB_NUMBER_THREADS PARALLEL_MAKE BB_HASHSERVE BB_HASHSERVE_UPSTREAM \
             http_proxy https_proxy ftp_proxy no_proxy NO_PROXY AGL_FLOATING AGL_SITE_CONF; do
    [ -n "${!var:-}" ] && args+=(-e "$var=${!var}")
  done
  # EULA_<MACHINE>=1 acceptance (see run_machine_setup_hooks) has to reach bitbake.
  for var in $(compgen -e | grep '^EULA_' || true); do args+=(-e "$var=${!var}"); done
  if [ -n "${AGL_SSTATE_DIR:-}" ]; then
    mkdir -p "$AGL_SSTATE_DIR"
    # Same path inside and out, so the value in env/local.conf is valid in both.
    args+=(-v "$AGL_SSTATE_DIR:$AGL_SSTATE_DIR" -e "AGL_SSTATE_DIR=$AGL_SSTATE_DIR")
  fi
  "$engine" "${args[@]}" "$image" "$@"
}

# Writes build/site.conf (bitbake-setup's shared site config, symlinked into every setup's
# conf/). Pre-creating it keeps the kas-era layout (build/downloads, build/sstate-cache -
# what the CI cache steps and docs refer to) instead of bitbake-setup's hidden dot-dirs.
bbsetup_write_siteconf() {
  mkdir -p "$REPO_ROOT/$BUILD_REL"
  {
    echo '# Generated by ci/scripts/_common.sh - edit AGL_SITE_CONF instead.'
    echo "DL_DIR ?= \"$BUILD_TOP/downloads\""
    echo "SSTATE_DIR ?= \"$BUILD_TOP/sstate-cache\""
    echo 'BB_HASHSERVE_DB_DIR ?= "${SSTATE_DIR}"'
    if [ -n "${AGL_SITE_CONF:-}" ]; then
      echo "# --- AGL_SITE_CONF=$AGL_SITE_CONF"
      cat "$AGL_SITE_CONF"
    fi
  } > "$REPO_ROOT/$BUILD_REL/site.conf"
}

# Path of the bitbake-setup executable (clones the bitbake revision pinned in kas/pins.yml
# once, into build/.bitbake-setup-tool; bitbake-setup ships inside bitbake).
bbsetup_bin() {
  if [ -n "${BITBAKE_SETUP:-}" ]; then echo "$BITBAKE_SETUP"; return; fi
  local url rev
  { read -r url; read -r rev; } < <(python3 "$REPO_ROOT/ci/scripts/_compose_setup.py" --bitbake-source)
  if ! agl_container test -x "$TOOL_DIR/bin/bitbake-setup" </dev/null >/dev/null 2>&1 \
     || [ "$(git -C "$REPO_ROOT/$BUILD_REL/.bitbake-setup-tool" rev-parse HEAD 2>/dev/null)" != "$rev" ]; then
    echo "bitbake-setup: bootstrapping bitbake $rev into $TOOL_DIR" >&2
    agl_container bash -ec "rm -rf '$TOOL_DIR' && git clone -q '$url' '$TOOL_DIR' && git -C '$TOOL_DIR' checkout -q '$rev'" </dev/null >&2
  fi
  echo "$TOOL_DIR/bin/bitbake-setup"
}

# bbsetup_sync CONFIG_JSON SETUP_NAME - create the setup (clone layers, write build/conf) or,
# if it exists, re-sync it with the freshly generated JSON (`bitbake-setup update` re-reads
# a local-path configuration, so changed features/pins/ci flag are picked up like kas did).
bbsetup_sync() {
  local config="$1" name="$2" bin
  bbsetup_write_siteconf
  bin="$(bbsetup_bin)"
  local -a common=("$bin" --setting default top-dir-prefix "$WORK_DIR"
                   --setting default top-dir-name "$BUILD_REL"
                   --setting default dl-dir "$BUILD_TOP/downloads")
  if [ -d "$REPO_ROOT/$BUILD_REL/$name/layers" ]; then
    # </dev/null: only bbsetup_shell may take stdin (piped commands must not be eaten here)
    agl_container "${common[@]}" update --setup-dir "$BUILD_TOP/$name" --update-bb-conf yes </dev/null
  else
    # $config is the host path the generator wrote; the container sees it under $WORK_DIR.
    agl_container "${common[@]}" init --non-interactive --no-init-vscode \
      --setup-dir-name "$name" "$WORK_DIR/$(basename "$config")" agl </dev/null
  fi
}

# bbsetup_run SETUP_NAME CMD - run CMD (a shell string) inside the setup's bitbake environment
# (kas shell -c equivalent: build/init-build-env sourced, cwd = the setup's build/ dir).
bbsetup_run() {
  local name="$1"; shift
  agl_container bash -c ". '$BUILD_TOP/$name/build/init-build-env' >/dev/null && $*" </dev/null
}

# bbsetup_shell SETUP_NAME - interactive shell in the setup (kas shell equivalent).
bbsetup_shell() {
  local name="$1"
  # (not --rcfile <(...): that would be a host fd path, invisible inside the container)
  agl_container bash -c ". '$BUILD_TOP/$name/build/init-build-env' >/dev/null && exec bash -i"
}
