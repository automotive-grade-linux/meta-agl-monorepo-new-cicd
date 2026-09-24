# SPDX-License-Identifier: MIT
FROM debian:stable-slim

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update -qq && apt-get install -y --no-install-recommends eatmydata

# Yocto/OE build host dependencies (matches docs.yoctoproject.org/dev/ref-manual/system-requirements.html)
# plus ca-certificates (HTTPS git/pip fetches) and diffutils (bitbake's HOSTTOOLS check
# requires `cmp`/`diff`, found missing during local validation of this Dockerfile's design).
RUN eatmydata apt-get install -qq -y --no-install-recommends \
      build-essential chrpath cpio debianutils diffstat diffutils file gawk gcc git \
      iputils-ping libacl1 libcrypt-dev locales python3 python3-git \
      python3-jinja2 python3-pexpect python3-pip python3-subunit socat \
      texinfo unzip wget xz-utils zstd python3-websockets \
      ca-certificates rsync \
    && eatmydata apt-get clean && rm -rf /var/lib/apt/lists/*

# en_US.UTF-8 locale, required by the build system
RUN echo "en_US.UTF-8 UTF-8" > /etc/locale.gen && locale-gen
ENV LANG=en_US.UTF-8

# dash -> bash: Yocto build scripts assume bash-compatible /bin/sh in places
RUN echo "dash dash/sh boolean false" | debconf-set-selections \
    && dpkg-reconfigure dash

# Documented for parity with reference Yocto CI containers; has no effect unless this
# container is run with elevated privileges (--privileged / --sysctl). Set on the CI
# runner/host instead - see docs/ci-setup.md.
RUN echo 'vm.max_map_count = 4048576' >> /etc/sysctl.conf

# kas/oelint-adv via pip, system-wide (not --user): once `kas shell`/`kas build` sources
# bitbake's build environment, PATH gets reset to a curated minimal set
# (oe-core/bitbake scripts + /usr/sbin:/usr/bin:/sbin:/bin) and HOME is redirected to a
# temp dir - a --user install under ~/.local/bin would be invisible there. System-wide
# lands these in /usr/local/bin, which survives the prune. Debian stable marks system
# Python as externally-managed (PEP 668); --break-system-packages is required.
RUN python3 -m pip install --break-system-packages --no-cache-dir kas oelint-adv

RUN useradd --create-home --uid 1000 --shell /bin/bash ci
WORKDIR /home/ci

COPY ci/docker/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# kas-container's own docker run invocation always passes --user=root and relies on the
# entrypoint to drop privileges (see entrypoint.sh) - this ENTRYPOINT is required for
# `kas-container checkout/build/shell <file>` to work, since it invokes the image as
# `<image> checkout <file>` expecting `kas` itself as entrypoint, not a full command line.
ENTRYPOINT ["/entrypoint.sh"]
CMD ["--help"]
