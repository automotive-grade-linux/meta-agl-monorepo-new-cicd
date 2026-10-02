---
name: bitbake-setup-operations
description: Hard-won operational knowledge for the bitbake-setup based build flow in this repo - how kas/*.yml are rendered into a bitbake-setup configuration (ci/scripts/_compose_setup.py), the setup directory layout under build/, the agl-ci-builder container wrapper (agl_container in ci/scripts/_common.sh), env vars (AGL_CONTAINER_IMAGE, AGL_SSTATE_DIR, AGL_SITE_CONF, AGL_FLOATING, AGL_BUILD_DIR), the meta-agl-setup fragment layer, the parity tools against the old kas flow, and how to safely run and debug long bitbake builds without corrupting them. Consult this BEFORE running make build/validate/setup/shell, scripts/aglsetup.sh, or any direct bitbake-setup/container invocation in this repo, and whenever such a command fails confusingly (wrong layers, fragment errors, missing tool inside the container, a build failing right after being interrupted, sstate suddenly at 0%). Also consult it before editing kas/*.yml (they are the data source) or meta-agl-setup/.
---

# bitbake-setup operational knowledge (this repo)

Fast-lookup reference. Everything here was verified against a real `make setup`/`bitbake -e`
comparison with the previous kas flow (qemux86-64 minimal and a pipewire+selinux+kuksa combo).

## 1. Environment (do this first)

| Need | How | Why |
|---|---|---|
| Docker | `sg docker -c '...'` around docker/make commands | Shell is often in the `docker` group in `/etc/group` but started before membership took effect. |
| Builder image | `docker build -f ci/docker/Dockerfile -t agl-ci-builder:dev .` (default image name; override `AGL_CONTAINER_IMAGE`) | Needs the generic entrypoint; an image from the kas era fails with `runuser: failed to execute shell`. No `kas` inside anymore. **Don't leave `KAS_CONTAINER_IMAGE=agl-ci-builder:dev` exported when you run real `kas-container`** (it then picks the kasless image). |
| Host python | `python3` + PyYAML | `_compose_setup.py` runs on the host. |

## 2. Flow and layout

- `make setup|validate|build|shell` -> `ci/scripts/*.sh` -> `ci/scripts/_compose_setup.py` renders
  `.bbsetup-<setup>.conf.json` (repo root, git-ignored, stable path so `bitbake-setup update` re-reads it)
  -> `bbsetup_sync` (`init` the first time, `update --update-bb-conf yes` afterwards) -> `bbsetup_run`
  / `bbsetup_shell` (`. build/<setup>/build/init-build-env` inside the container).
- `<setup>` = `<machine>[-<feature>...]`; dirs: `build/<setup>/{layers,build,config}`, shared
  `build/site.conf` (DL_DIR `build/downloads`, SSTATE_DIR `build/sstate-cache` - kas-era paths, CI caches
  rely on them), `build/.bitbake-setup-tool` (bitbake clone at the SHA in `kas/pins.yml`, provides
  `bitbake-setup`). Artifacts: `build/<setup>/build/tmp/deploy`, copied to `build/artifacts/`.
- Inside the container the repo is `/work` and the config JSON must be addressed as `/work/.bbsetup-*.json`
  (the generator prints the host path; `bbsetup_sync` translates). In-repo layers (meta-agl*, meta-agl-setup) are
  `local` sources = symlinks under `layers/`; `external/*`/`bsp/*` paths are kept as `layers/external/...`.
- `AGL_NO_CONTAINER=1` runs everything on the host (then `/work` is the repo root); `BITBAKE_SETUP=<path>` skips the bootstrap.

## 3. kas/*.yml is the data source - and exact kas semantics are reproduced

`_compose_setup.py` merges `kas/base.yml`, `machine/<m>.yml`, `feature/<f>.yml` (+`sstate-shared.yml` if
`AGL_SSTATE_DIR`, `ci-only.yml` if `CI=true`) and `pins.yml` (or `floating.yml` if `AGL_FLOATING`) like kas:
includes first, later wins. Facts that bit during the migration (all from kas's own source):
- `bblayers.conf` order = sorted by **(layer prio desc, repo name, layer name)**, not merge order; a repo
  with no `layers:` key = one root layer (`""`), `layers: {}` = none (bitbake). BBLAYERS order decides BBPATH/bbappend order.
- `local.conf` blocks = sorted **by key** (matters: `AGL_FEATURES +=` append order).
- `bblayers_conf_header` (`LCONF_VERSION = "6"`) is dropped: setting it makes oe-core's sanity auto-migration crash
  (`TypeError: list indices must be integers`) because bitbake-setup's bblayers.conf has no such line.
- `DISTRO` comes from the builtin `distro/agl` fragment; a plain `DISTRO =` in a fragment is fatal, so it is filtered out.
- Run `python3 ci/scripts/_check_parity.py` after any change to `kas/` or `_compose_setup.py`: it compares every
  matrix combination against `kas dump` (layer order via kas's own `RepoLayer` sort, pins, fragments order). Needs the `kas` CLI.
  NB: `kas dump` clones `external/`/`bsp/` as a side effect.

## 4. Fragments (meta-agl-setup)

- `meta-agl-setup/conf/fragments/agl/*.conf` are **generated** (`python3 ci/scripts/_compose_setup.py --write-fragments`),
  one per kas `local_conf_header` key; `make validate` runs `--check-fragments`. Don't edit by hand; don't regenerate while a build runs
  (bitbake watches config files).
- Fragment ids are `<BBFILE_COLLECTION>/<subdir>/<file>` = `agl-setup/agl/<key>` (NOT `agl-setup/<key>`).
- bitbake-setup runs `bitbake-config-build enable-fragment`, which parses **every** fragment of every layer standalone, so a
  `require` of a feature layer's `.inc` aborts when that layer isn't in the setup -> generated fragments use `include`
  (`--check-fragments` verifies each included file exists in a vendored layer). Each fragment needs `BB_CONF_FRAGMENT_SUMMARY`
  and `BB_CONF_FRAGMENT_DESCRIPTION`.
- The layer has no BBPATH entry on purpose (fragments are found through the layer dir).
- Fragments are `OE_FRAGMENTS` in `conf/toolcfg.conf`, parsed right after `local.conf` - same slot kas's local.conf blocks had.

## 5. Parity comparison recipe (kas vs bitbake-setup)

`bitbake -e <target> > env.txt` in both setups, then `python3 ci/scripts/_diff_bbenv.py kas.txt bbs.txt <setup>`;
its KNOWN list documents the only intended differences (extra recipe-less `agl-setup` layer, `LCONF_VERSION`,
kas's proxy env, ordering-only noise). The kas side: `KAS_BUILD_DIR=<dir> kas-container shell <files> -c 'bitbake -e ...'`
with the upstream kas image (unset `KAS_CONTAINER_IMAGE`). A full `agl-demo` `bitbake -e` currently fails in BOTH flows
at the pinned meta-flutter (`libwebrtc` LICENSE format QA error) - an upstream pin issue, not a migration bug.

## 6. Running and debugging long bitbake builds safely

**Never run a long build as a foreground command subject to a wall-clock kill** - SIGTERM mid-compile corrupts artifacts
(`archive has no index; run ranlib`, git mirror with invalid HEAD -> misleading `Unable to find revision`). Launch detached:
```sh
setsid bash -c "sg docker -c 'make build MACHINE=... FEATURES=...' > log 2>&1; echo EXIT=\$? >> log" < /dev/null > /dev/null 2>&1 & disown
```
then poll the log (`until grep -q '^EXIT' log; do sleep 5; done`). After an interrupted build, suspect corruption before a
regression: `bbsetup_run <setup> "bitbake -c clean <recipe>"`, or remove the corrupted `build/downloads/git2/<mirror>` after
`git fsck --full`. `Sstate summary: ... 0% match` at start = full rebuild coming (expected after moving layers/changing
the setup path, since layer paths are signature inputs); a high match % is a normal incremental build.
Repeated `make setup` on an existing setup leaves `build/<setup>/build/conf-backup.<timestamp>` dirs - harmless.
