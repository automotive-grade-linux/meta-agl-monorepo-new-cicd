# AGENTS.md

This repo **is the AGL source**: `meta-agl`, `meta-agl-demo`, `meta-agl-devel` are the real,
vendored AGL distro layers (git subtree, full history), tracked and committed here, not pointers
to it elsewhere. `external/` and `bsp/` are third-party (non-AGL) layers fetched at build time
instead (git-ignored, not committed). `kas/` holds the YAML fragments (repos, layers, features,
pins) that `ci/scripts/_compose_setup.py` renders into a `bitbake-setup` configuration; `ci/` has
the CI scripts plus the curated build matrix (`ci/build-matrix.yaml`); `meta-agl-setup/` is the
small generated layer holding the config fragments. The kas *tool* is no longer used to build.

## Commands

- `make setup|validate|build|shell MACHINE=<m> FEATURES=<f1,f2> [TARGET=<t>] [EXTRA_FEATURES=<f>]`
  — the curated path. `FEATURES` must exactly match a `ci/build-matrix.yaml` entry (exact-set
  equality, not subset) or `make` fails with a candidate list.
- `scripts/aglsetup.sh -m <machine> <feature> [<feature> ...]` — free-form local exploration,
  drops into an interactive shell inside a bitbake-setup setup (container). No matrix entry required; any machine with a
  `kas/machine/<name>.yml` and any features each with a `kas/feature/<name>.yml` work in any
  combination. Run `bitbake <target>` yourself once inside. Also reachable via
  `meta-agl/scripts/aglsetup.sh` (a symlink back to this file, for classic-AGL muscle memory).
- `docs/setup.md` is the canonical developer doc — read it before assuming how something works.

## Before running any setup/bitbake command

1. Build the builder image first if missing or stale: `docker build -f ci/docker/Dockerfile -t
   agl-ci-builder:dev .` (`agl-ci-builder:dev` is the default; override with
   `AGL_CONTAINER_IMAGE`). An image from the kas era fails with `runuser: failed to execute
   shell` — rebuild it. Don't leave `KAS_CONTAINER_IMAGE` pointing at it when running real `kas`.
2. If `docker` commands fail with a permission error despite the user being in the `docker`
   group, wrap the command in `sg docker -c '...'` instead (stale group membership in the
   current shell, not a real permissions problem).
3. **Never run a long bitbake build as a foreground command subject to a wall-clock kill** — a
   forced SIGTERM mid-compile corrupts in-progress artifacts (broken archives, invalid git mirror
   HEADs) that then cause confusing failures on the *next* attempt. Launch it detached instead:
   `setsid bash -c '... > log 2>&1' < /dev/null > /dev/null 2>&1 & disown`, then poll/tail the
   log file separately.
4. A full rebuild (sstate at 0% match) right after moving a layer, a recipe or the setup
   directory is expected, not a bug — `BBPATH` and recipe `FILE` paths are both bitbake signature
   inputs.
5. After changing anything in `kas/` or `ci/scripts/_compose_setup.py`: run `python3
   ci/scripts/_compose_setup.py --write-fragments` (regenerates `meta-agl-setup/conf/fragments/`,
   never edit those by hand) and `python3 ci/scripts/_check_parity.py` (compares every matrix
   combination against `kas dump`; needs the `kas` CLI).

For full detail on either of the above, consult the `bitbake-setup-operations` and
`yocto-bitbake` Claude Code skills in `.claude/skills/` — they carry the verified specifics
(exact env var names, setup layout, fragment naming rules, how to recover from a corrupted build,
etc.) that this file deliberately leaves out to stay short.

## Conventions

- Don't hand-edit anything under `meta-agl/`, `meta-agl-demo/`, `meta-agl-devel/`, `external/`,
  `bsp/` as if it were this repo's own code — it's vendored/fetched content with its own
  upstream and licensing (see `LICENSE`). Fixes to vendored recipes go through a `.bbappend` in
  the tracked layers, not an in-place edit.
- `ci/scripts/` is CI-internal plumbing (matrix-gated, driven by the `Makefile`); `scripts/` at
  the repo root is the free-form/local-exploration front door. Keep that separation when adding
  new tooling.
- Only commit when explicitly asked.

## Attribution

AGL tracks AI involvement in commits. Any commit an AI tool contributed to must carry a trailer:

```
Assisted-by: AGENT_NAME:MODEL_VERSION [TOOL1] [TOOL2]
```

`AGENT_NAME` is the AI tool/framework, `MODEL_VERSION` the specific model used, and `[TOOL1]
[TOOL2]` are optional specialized analysis tools involved (e.g. `coccinelle`, `sparse`, `smatch`,
`clang-tidy`) — omit basic dev tools (git, gcc, make, editors). Example used throughout this
repo's history: `Assisted-by: Claude Code:claude-sonnet-5`.

Do not add Co-authored-by: .

## General
Be concise. Use https://raw.githubusercontent.com/DietrichGebert/ponytail/refs/heads/main/AGENTS.md .