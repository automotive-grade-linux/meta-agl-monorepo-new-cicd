# AGENTS.md

This repo **is the AGL source**: `meta-agl`, `meta-agl-demo`, `meta-agl-devel` are the real,
vendored AGL distro layers (git subtree, full history), tracked and committed here, not pointers
to it elsewhere. `external/` and `bsp/` are third-party (non-AGL) layers fetched by `kas` at
build time instead (git-ignored, not committed). `kas/` holds the kas YAML fragments and `ci/`
the CI scripts plus the curated build matrix (`ci/build-matrix.yaml`) that build that source.

## Commands

- `make setup|validate|build|shell MACHINE=<m> FEATURES=<f1,f2> [TARGET=<t>] [EXTRA_FEATURES=<f>]`
  — the curated path. `FEATURES` must exactly match a `ci/build-matrix.yaml` entry (exact-set
  equality, not subset) or `make` fails with a candidate list.
- `scripts/aglsetup.sh -m <machine> <feature> [<feature> ...]` — free-form local exploration,
  drops into an interactive `kas-container shell`. No matrix entry required; any machine with a
  `kas/machine/<name>.yml` and any features each with a `kas/feature/<name>.yml` work in any
  combination. Run `bitbake <target>` yourself once inside. Also reachable via
  `meta-agl/scripts/aglsetup.sh` (a symlink back to this file, for classic-AGL muscle memory).
- `docs/setup.md` is the canonical developer doc — read it before assuming how something works.

## Before running any kas/bitbake command

1. `export KAS_CONTAINER_IMAGE=agl-ci-builder:dev` (build it first if missing: `docker build -f
   ci/docker/Dockerfile -t agl-ci-builder:dev .`). Without this, `kas-container` uses the
   upstream `ghcr.io/siemens/kas/kas` image, which lacks `oelint-adv` — `make validate`'s
   `recipe-lint` step fails with a confusing "No such file or directory".
2. If `docker` commands fail with a permission error despite the user being in the `docker`
   group, wrap the command in `sg docker -c '...'` instead (stale group membership in the
   current shell, not a real permissions problem).
3. **Never run a long bitbake build as a foreground command subject to a wall-clock kill** — a
   forced SIGTERM mid-compile corrupts in-progress artifacts (broken archives, invalid git mirror
   HEADs) that then cause confusing failures on the *next* attempt. Launch it detached instead:
   `setsid bash -c '... > log 2>&1' < /dev/null > /dev/null 2>&1 & disown`, then poll/tail the
   log file separately.
4. A full rebuild (sstate at 0% match) right after moving a `kas/` fragment, a layer, or a recipe
   is expected, not a bug — `BBPATH` and recipe `FILE` paths are both bitbake signature inputs.

For full detail on either of the above, consult the `kas-operations` and `yocto-bitbake` Claude
Code skills in `.claude/skills/` — they carry the verified specifics (exact env var names,
kas-container's command grammar, how to recover from a corrupted build, etc.) that this file
deliberately leaves out to stay short.

## Conventions

- Don't hand-edit anything under `meta-agl/`, `meta-agl-demo/`, `meta-agl-devel/`, `external/`,
  `bsp/` as if it were this repo's own code — it's vendored/fetched content with its own
  upstream and licensing (see `LICENSE`). Fixes to vendored recipes go through a `.bbappend` in
  the tracked layers, not an in-place edit.
- `ci/scripts/` is CI-internal plumbing (matrix-gated, driven by the `Makefile`); `scripts/` at
  the repo root is the free-form/local-exploration front door. Keep that separation when adding
  new tooling.
- Only commit when explicitly asked.
