# Developer setup

## Prerequisites

- Docker or rootless Podman (for `kas-container`)
- `pip install kas` (for `make lock`, or to run `kas`/`kas-container` directly)

## Building locally

```sh
make help
make build MACHINE=qemux86-64 FEATURES=agl-demo
```

`MACHINE`/`FEATURES` must match one of `ci/build-matrix.yaml`'s curated `(target, features,
machines)` entries (comma-separated feature list) - see [`ci/build-matrix.yaml`](../ci/build-matrix.yaml)
and `ci/scripts/_matrix.py` for the schema. If several images share the same `MACHINE`+`FEATURES`
(e.g. the `agl-demo` group's 5 images on one machine), add `TARGET=<image>` to disambiguate - the
error message lists the candidates. `make build` runs `setup` (kas checkout, EULA handling, the
h3ulcb/m3ulcb proprietary-package hook) then the actual build via `kas-container`, using the
Dockerfile at `ci/docker/Dockerfile` (set `KAS_CONTAINER_IMAGE` to point at a locally built or
published image; see `ci/docker/Dockerfile`).

There's no per-combination kas file: `ci/scripts/_compose_kasfiles.py` computes the colon-joined
list directly from `MACHINE`+`FEATURES` (`ci/kas/base.yml:ci/kas/machine/<m>.yml:
ci/kas/feature/<f>.yml:...:ci/kas/pins.yml`). `ci/kas/pins.yml` is one consolidated file pinning
every external repo's commit - no per-machine/per-feature lockfile to keep in sync.

`ci/build-matrix.yaml` itself is keyed by image (`images:`, one entry per bitbake `target:`, each
with a `machines:` list - not one row per `(machine, target)` pair) - `ci/scripts/_matrix.py`'s
`expand_entries()` flattens it into that per-row shape for lookups, and is the one place every
consumer (this script, `make validate`, both CI workflows) shares. It also enforces
`INCOMPATIBLE_FEATURES` (currently: `agl-kvm`/`agl-xen` can't appear in the same entry - two
hypervisor backends can't coexist on one image) - `aglsetup.sh` itself never checked this
(purely additive), so this repo does instead. `make validate`/CI run it as the `matrix-validate`
check, and it's runnable standalone: `python3 ci/scripts/_matrix.py`.

`agl-devel` (passwordless login, useful at your desk) is deliberately **not** part of any matrix
entry's `FEATURES` - add it yourself on top of any matrix entry with `EXTRA_FEATURES=agl-devel`,
e.g. `make build MACHINE=qemux86-64 FEATURES=agl-demo TARGET=agl-ivi-demo-flutter
EXTRA_FEATURES=agl-devel`. CI builds get the equivalent automatically (hardware-in-the-loop
testing needs it every time) via `ci/kas/ci-only.yml`, colon-joined whenever `CI=true` - you never
need to (and shouldn't) set that yourself.

Other targets: `make validate` (the layer QA suite), `make shell` (interactive kas-container
shell), `make lock` (resolve latest upstream commits so you can hand-copy bumps into
`ci/kas/pins.yml`), `make clean`.

## h3ulcb/m3ulcb: proprietary R-Car packages (local build only, not CI)

`h3ulcb`, `h3ulcb-kf`, `m3ulcb`, `m3ulcb-kf` need Renesas's proprietary R-Car Gen3 graphics/
multimedia packages to build at all. **CI never builds these 4** (`ci/build-matrix.yaml` gives them
`tiers: []`) - there's no way to get redistributable credentials for proprietary, license-gated
binaries into a CI runner. CI only builds the `-nogfx` variants (`h3ulcb-nogfx`, `m3ulcb-nogfx`),
which don't need them.

To build the non-`-nogfx` variants yourself:

1. Download these two files from Renesas (requires a free Renesas account and accepting their
   EULA - we can't do this for you, and can't redistribute the files ourselves):
   <https://www.renesas.com/us/en/application/automotive/r-car-h3-m3-documents-software>
   - `R-Car_Gen3_Series_Evaluation_Software_Package_for_Linux-20220121.zip`
   - `R-Car_Gen3_Series_Evaluation_Software_Package_of_Linux_Drivers-20220121.zip`
2. Place both files in your `$XDG_DOWNLOAD_DIR` (or plain `~/Downloads` if you haven't configured
   XDG user dirs) - the same convention `aglsetup.sh` always used, nothing kas-specific here.
3. `make build MACHINE=h3ulcb` (or `h3ulcb-kf`/`m3ulcb`/`m3ulcb-kf`) - `setup.sh` detects these
   machines and runs the extraction/install step (`copy_mm_packages`, sourced from
   `layers/meta-agl/meta-agl-bsp/meta-rcar-gen3/scripts/setup_mm_packages.sh`) automatically
   before the build, on your host (not inside the container) so it can see your real `~/Downloads`.
   If the zips aren't found it prints exactly which files/URL are missing and continues (rather
   than aborting `make build` outright) - the actual bitbake build will then fail with its own
   missing-proprietary-recipe errors, which is expected until the zips are in place.

This step is idempotent - once extracted to `layers/binary-tmp/`, reruns skip straight past it
(delete that directory to force re-extraction, e.g. after downloading updated packages).

## Floating on branch tips instead of pinned commits

By default every build uses `ci/kas/pins.yml` (fixed commits). To float on the tip of each
repo's declared branch instead (useful for testing against upstream HEAD), set:

```sh
export AGL_FLOATING=1
```

before `make setup`/`make build`/`make validate`/`make shell`. This swaps in `ci/kas/floating.yml`
(no commit overrides) instead of `ci/kas/pins.yml` - CI never sets this, so CI builds always stay
reproducible.

To actually bump `ci/kas/pins.yml` itself (resolve every repo's current tip and write the new
commits in, preserving the file's comments/grouping):

```sh
make pin-update
```

This runs `kas lock --update` against every machine+feature fragment at once (via
`ci/kas/floating.yml`), then `ci/scripts/pin-update-helper.py` does the targeted in-place update
and prints a summary of what changed. **Review the diff before committing** - like any dependency
bump, it can pull in real upstream breakage.

## Sharing sstate-cache / a personal site.conf across builds

By default `SSTATE_DIR` is per-checkout (`build/sstate-cache`), matching what the CI cache
actions target. To reuse a persistent sstate cache across machines/features/checkouts on your
own workstation instead, set:

```sh
export AGL_SSTATE_DIR=$HOME/.yocto/sstate-cache
```

before `make setup`/`make build`/`make validate`/`make shell`. It's bind-mounted into the
container and wired into kas via `ci/kas/local/sstate-shared.yml` (only included when this env
var is set - CI is unaffected).

If you already keep a personal `site.conf` (e.g. with your own `SSTATE_DIR`, mirrors, or other
site-local tuning), bitbake auto-includes `conf/site.conf` from the build dir with no kas config
changes needed — just bind-mount your file in:

```sh
export AGL_SITE_CONF=$HOME/.yocto/site.conf
```

Both can be set together; if your `site.conf` also sets `SSTATE_DIR`, keep it consistent with
`AGL_SSTATE_DIR` yourself — the mount is verbatim, nothing here parses `site.conf`.

## Verified: building a full demo image

This exact flow has been run end-to-end successfully (`qemux86-64`, `agl-demo`+`agl-devel`,
target `agl-ivi-demo-flutter`):

```sh
export KAS_CONTAINER_IMAGE=agl-ci-builder:dev   # or your published image
make build MACHINE=qemux86-64 FEATURES=agl-demo TARGET=agl-ivi-demo-flutter EXTRA_FEATURES=agl-devel
```

(`agl-ivi-demo-flutter` is unambiguous for `qemux86-64`+`agl-demo` today since it's the only entry
with `push-pr` in its tiers, but `TARGET=` is still recommended for clarity - the other 4
`agl-demo`-group images on `qemux86-64` need it to disambiguate. To try a target ad hoc without a
matrix entry at all, invoke `kas-container shell <kasfiles> -c "bitbake <target>"` directly -
`ci/scripts/_compose_kasfiles.py --machine ... --features ... --target ...` prints the `<kasfiles>`
list.)

**What to expect:**
- A full image build (not just a minimal one) is CPU/RAM/disk heavy: several hours even on a modern
  multi-core host, more on a constrained one. `ci/kas/base.yml` caps `BB_NUMBER_THREADS`/
  `PARALLEL_MAKE` at 4 by default — raise it (edit the two lines directly, no config knob exists yet)
  if your host has cores to spare and isn't shared with anything else.
- The build is fully resumable: if it's interrupted (container killed, host reboot, `docker` daemon
  restarted), just rerun the same `make build` command. `sstate-cache` means only genuinely
  incomplete/invalidated tasks get redone.
- **Known gotcha**: if you resume a build across *separate* `kas-container`/`docker` invocations
  (rather than one continuous run), you may see spurious `ERROR: ... basehash value changed ...
  metadata is not deterministic` failures from Flutter's `do_archive_pub_cache`/`do_restore_pub_cache`
  tasks, even though the actual image gets built fine (check `build/tmp/deploy/images/<machine>/` -
  the `.ext4`/manifest/SPDX files will be there with fresh timestamps regardless). This traces to
  `kas-container` randomizing `$HOME` per invocation, which some Flutter/Dart pub-cache tooling paths
  key off; a single uninterrupted `bitbake` run doesn't hit it. If you see this, just rerun once more,
  uninterrupted — it clears immediately since almost everything is already in sstate.
- A pre-existing upstream issue was found and fixed while validating this: `meta-openembedded`'s
  `grpc_1.80.0.bb` pins a commit that GitHub's `grpc/grpc` `v1.80.x` branch has since been force-pushed
  past (the tag itself is still valid, just no longer reachable from that branch name, which bitbake's
  git fetcher requires by default). Fixed via a bbappend at
  `layers/meta-agl/meta-agl-core/recipes-devtools/grpc/grpc_%.bbappend` adding `nobranch=1`. Nothing
  you need to do - already in the vendored tree.

## Exploring machine/feature combinations interactively

`kas menu ci/kconfig/Kconfig` opens an interactive Kconfig-style menu (machine choice + AGL
feature toggles, mirroring `aglsetup.sh`'s old `included.dep` pull-in graph via `select`, and the
`agl-kvm` → `qemux86-64`-only constraint via `depends on`). This is for local exploration only —
CI never uses it. Once you've found a combination worth keeping, add it as a new entry in
`ci/build-matrix.yaml` (no new kas file needed - see "Adding a new machine" below for the one
case that does, a genuinely new machine's BSP layers).

## Repository layout

See [`WIP.md`](../WIP.md) at the repo root for the full architecture decision log, the
`aglsetup.sh` → kas mapping, and the matrix-tooling design.

## Adding a new machine

1. Add `ci/kas/machine/<name>.yml` (see existing ones for the `path:`-only vendored vs.
   `url:`+`branch:` external-repo pattern).
2. `make lock MACHINE=<name>` to resolve its new repo(s)' latest commits, then hand-copy them
   into `ci/kas/pins.yml`. If it needs EULA acceptance to build (proprietary/license-gated BSP
   bits), add it to `ci/build-matrix.yaml`'s `machine_eula:` table.
3. Add `<name>` to the `machines:` list of every `images:` entry it should build (a bare string
   to use that image's default `tiers:`, or `<name>: {tiers: [...]}` to override).
4. `make build MACHINE=<name>` to verify before committing.

## Adding a new feature

1. Add `ci/kas/feature/<name>.yml`, mirroring the matching
   `layers/*/templates/feature/<name>/50_local.conf.inc`+`50_bblayers.conf.inc` (`header.includes:`
   whatever that feature's `included.dep` lists as other `ci/kas/feature/*.yml` files).
2. If it introduces a new external (non-vendored) repo, `make lock` and hand-copy the resolved
   commit into `ci/kas/pins.yml`, same as adding a machine.
3. If it's mutually exclusive with an existing feature (e.g. two backends that can't coexist on
   one image, like `agl-kvm`/`agl-xen`), add the pair to `INCOMPATIBLE_FEATURES` in
   `ci/scripts/_matrix.py` - `aglsetup.sh` never checked this itself, so nothing else will.
4. Add a curated `images:` entry (or extend an existing one's `machines:` list) in
   `ci/build-matrix.yaml`.
