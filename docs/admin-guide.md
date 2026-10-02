# Admin / release-engineer guide

Operational notes for whoever runs this repo's CI infrastructure or self-hosted build workers —
distinct from [`setup.md`](setup.md), which is developer-facing. Architecture rationale lives in
[`WIP.md`](../WIP.md); this doc is what you need to actually stand up and operate a builder.

## Container runtime requirements

The build container (`ci/docker/Dockerfile`) is built and run via [`kas-container`](
https://kas.readthedocs.io/en/latest/userguide/kas-container.html), which wraps Docker or rootless
Podman. Whatever host/CI runner runs this needs a **fully functional container runtime with real
image-layer extraction rights** — this was validated the hard way in a restricted sandbox and is
worth stating explicitly:

- **`CAP_SYS_ADMIN` and an unfiltered seccomp profile are required.** A container missing these
  can start `dockerd` and even pull images, but image layer extraction fails
  (`unshare: operation not permitted`) regardless of storage driver. If Docker-in-Docker is itself
  running inside another container (nested CI runners, sandboxed dev environments), that **outer**
  container needs `--privileged`, or explicitly `--cap-add=SYS_ADMIN --cap-add=NET_ADMIN
  --security-opt seccomp=unconfined --security-opt apparmor=unconfined`. A more secure alternative
  to full `--privileged` for nested Docker: the [sysbox runtime](https://github.com/nestybox/sysbox).
- **If the host's own root filesystem is already overlayfs** (common for containerized CI
  runners/sandboxes), Docker's default `overlay2` storage driver will fail with
  `failed to mount ... fstype: overlay ... err: invalid argument` (overlay-on-overlay isn't
  supported by the kernel). Start `dockerd --storage-driver=vfs` instead. `vfs` is slower and uses
  more disk (no shared layer dedup) but works on any filesystem — acceptable for CI, not
  recommended for a bare-metal build farm with a normal filesystem, where `overlay2` should be used.
- **iptables/NAT**: `dockerd`'s default bridge networking needs `iptables --wait -t nat -N DOCKER`,
  which needs real root/`CAP_NET_ADMIN`. If that's unavailable (again, common in nested/sandboxed
  runners), start with `--iptables=false --bridge=none` — containers then have no outbound network
  of their own via the bridge; not a problem here since `kas-container` publishes no ports and the
  build container only needs outbound HTTPS (git/pip/apt), which still works via the daemon's own
  host networking for image pulls, but **verify build-time network egress inside the container**
  (`docker run --rm <image> curl -sI https://github.com`) before relying on this in production -
  bridgeless networking has different implications for containers that need their own egress, which
  ordinary `docker build`/`docker run` (not just image pulls) does.
- The build container runs as **root inside, dropping to a non-root `ci` user via
  `ci/docker/entrypoint.sh`**, which reads `USER_ID`/`GROUP_ID` env vars that `kas-container` sets
  automatically from the *calling host user* — files written into the bind-mounted repo end up
  owned by whichever host user invoked `kas-container`, not root. No manual `chown` should ever be
  needed after a build; if you see root-owned files under `build/`, the entrypoint isn't being
  invoked correctly (check `docker inspect <image> | grep Entrypoint` - it must be
  `["/entrypoint.sh"]`, not absent/overridden).

## Sudo / privilege boundary

Starting/stopping `dockerd` itself needs root. This repo's own CI (GitHub Actions/GitLab CI runners)
handles that at the platform level — you won't touch it. On a self-managed build worker, whoever
administers it should decide once whether `dockerd` runs as a persistent system service (normal
production setup, e.g. via systemd) or is started per-job — this repo's scripts never start/stop
`dockerd` themselves, that's infrastructure-layer, not build-layer.

## Resource sizing

Measured on an actual full build (`qemux86-64`, `agl-demo`+`agl-devel`, target
`agl-ivi-demo-flutter` - includes Flutter + Qt6 toolchains, LLVM/clang, Rust, the full multimedia
stack):

| Resource | Observed |
|---|---|
| `build/sstate-cache` | ~19 GB |
| `build/downloads` | ~36 GB |
| `build/tmp` (work area) | ~295 GB |
| **`build/` total (measured)** | **348 GB** for this one image target |
| **Total recommended free disk** | **400+ GB** per concurrent build to leave headroom; `build/tmp` dominates and is safe to `make clean` between unrelated builds (unlike `sstate-cache`/`downloads`, which are worth keeping). A full 22-machine matrix run needs proportionally more unless `AGL_SSTATE_DIR`/shared downloads mitigate it - `build/tmp` itself isn't shareable across machines/targets. |
| Wall-clock time (this build) | ~3 hours on 8 cores capped to `BB_NUMBER_THREADS=4`/`PARALLEL_MAKE=-j 4`; a from-scratch build with no sstate reuse will take substantially longer (this run reused sstate for setup/toolchain-adjacent tasks across resumed attempts) |
| Task count | ~12,300 bitbake tasks for this one image target |

**Parallelism**: `kas/base.yml` hardcodes `BB_NUMBER_THREADS ?= "4"` / `PARALLEL_MAKE ?= "-j 4"`
rather than scaling to host core count - this was a deliberate choice to avoid overloading shared
build infrastructure. **Raise this for dedicated build workers** with more headroom (edit those two
lines directly; there's no separate override knob yet - add one via kas's `env:` passthrough
mechanism, same pattern as `AGL_SSTATE_DIR` in `ci/scripts/_kas_runtime_args.sh`, if per-worker
tuning becomes a real need).

**Shared sstate for a fleet of workers**: see `AGL_SSTATE_DIR`/`AGL_SITE_CONF` in
[`setup.md`](setup.md#sharing-sstate-cache--a-personal-siteconf-across-builds) - the same mechanism
works for a shared NFS-mounted or otherwise centralized sstate directory across a build farm, not
just a single developer's workstation. Point every worker's `AGL_SSTATE_DIR` at the same shared
path.

## Known upstream issues and fixes already applied

- **`grpc_1.80.0.bb` (meta-openembedded) branch-pin breakage**: upstream `grpc/grpc`'s `v1.80.x`
  branch was force-pushed past the commit meta-openembedded pins (the tag `v1.80.0` itself is still
  valid). Fixed via `meta-agl/meta-agl-core/recipes-devtools/grpc/grpc_%.bbappend`
  (`nobranch=1`). If a future `meta-openembedded` bump moves off this SRCREV, this bbappend becomes a
  no-op (harmless) or needs its own SRCREV bump to match - check it if grpc-related fetch failures
  reappear after updating `kas/pins.yml`.
- **Flutter pub-cache task signature instability across resumed builds**: see the "Known gotcha" in
  [`setup.md`](setup.md#verified-building-a-full-demo-image). Only affects builds resumed across
  separate container invocations (e.g. after a worker restart mid-build); a normal single-invocation
  CI job is not expected to hit this. If it does show up in CI, it indicates the job's container was
  restarted mid-build (worth investigating why), not a code regression.
- **`PermissionError: [Errno 13] Permission denied: '/work/build'` from `kas` on Docker hosts with
  SELinux enabled** (`"selinux-enabled": true` in `/etc/docker/daemon.json` - seen on a real
  developer workstation, not hypothetical): `kas-container`'s own script only adds
  `--security-opt label=disable` for the podman engine, never for docker - so with SELinux
  enforcing, the kernel denies the container's bind-mounted access to `/work` even though Unix
  owner/group/mode all look correct, which is easy to misdiagnose as a UID mismatch (it isn't -
  `USER_ID`/`GROUP_ID` passthrough and `ci/docker/entrypoint.sh`'s remap both work correctly here).
  Fixed in `ci/scripts/_kas_runtime_args.sh`'s `kas_runtime_args()`, which now always adds
  `--security-opt label=disable` itself regardless of engine - harmless no-op on non-SELinux Docker
  and a harmless duplicate on podman (which already gets it from `kas-container`).
- **`kas` fails with `"22 is not valid under any of the given schemas"`** (config file validation
  error on `kas/base.yml` or any other fragment): the image being used has an older `kas` baked
  in than this repo's `header: version: 22` config files need - kas only understands schema
  version 22 from release 5.3 onward (confirmed by inspecting kas's own `schema-kas.json` across
  PyPI releases). Root cause was `ci/docker/Dockerfile`'s
  `pip install kas oelint-adv` being unpinned, so a locally-built image silently baked in whatever
  was latest on PyPI the day it was built - if that predates kas 5.4 (or pip resolved an older
  cached wheel), this is what you get, and it looks nothing like a version problem from the error
  text alone. Now pinned (`kas==5.5 oelint-adv==9.11.2`, the versions this project has actually
  been validated against). **Only affects locally-built developer images** - CI always rebuilds and
  pushes a fresh image on every run (see "CI-specific operational notes" below), so it was never at
  risk here. Anyone with a pre-existing local `agl-ci-builder` image needs to rebuild it once:
  `docker build -f ci/docker/Dockerfile -t agl-ci-builder:dev .` (see `setup.md`).

## CI-specific operational notes

- **Image registry**: `ci/docker/Dockerfile` is built and pushed to a platform-native registry
  (GHCR for GitHub Actions, GitLab Container Registry for GitLab CI) per `WIP.md`'s architecture -
  no shared registry between the two. Rebuild-and-push happens on every CI run with layer caching;
  there's no separate scheduled rebuild.
- **`yocto-check-layer` in `make validate`** currently takes ~75 minutes and exercises a full
  per-sublayer world-signature check - expect this to dominate `make validate`'s runtime. It's
  deliberately run against a bare `_validate-base` combo (see `ci/scripts/validate.sh`) with no AGL
  layers pre-loaded, to avoid a duplicate-`BBFILE_COLLECTIONS` conflict; don't "simplify" this by
  pointing it at a machine combo instead.
- **`--privileged` / `CAP_SYS_ADMIN` requirement above applies to CI runners too** if you're running
  self-hosted GitHub Actions/GitLab CI runners inside containers (common in Kubernetes-based runner
  fleets) rather than on bare VMs. GitHub-hosted and GitLab.com shared runners already provide a
  working Docker environment and need no special configuration.
