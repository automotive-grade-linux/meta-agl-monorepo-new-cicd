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

# kas/oelint-adv via pip, system-wide (not --user): once `kas shell`/`kas build` sources
# bitbake's build environment, PATH gets reset to a curated minimal set
# (oe-core/bitbake scripts + /usr/sbin:/usr/bin:/sbin:/bin) and HOME is redirected to a
# temp dir - a --user install under ~/.local/bin would be invisible there. System-wide
# lands these in /usr/local/bin, which survives the prune. Debian stable marks system
# Python as externally-managed (PEP 668); --break-system-packages is required.
#
# Pinned, not left to "whatever's latest at image-build time": this repo's ci/kas/*.yml
# files declare `header: version: 23`, a kas config-schema version kas only understands
# from 5.4 onward (5.3's schema caps at 22 - confirmed by inspecting kas's own
# schema-kas.json across releases). An unpinned `pip install kas` silently bakes in
# whatever was on PyPI the day someone (re)built this image locally - if that happened
# before a compatible kas release existed, or their pip resolved an older cached wheel,
# `kas` fails with "23 is not valid under any of the given schemas" despite everything
# else being correct (seen on a real developer workstation, see WIP.md "round 13"). Same
# reproducibility argument applies to oelint-adv (validate.sh's recipe-lint check
# shouldn't silently vary by whichever version happened to be latest at build time
# either) - pinned to what this project has actually been validated against.
RUN python3 -m pip install --break-system-packages --no-cache-dir kas==5.5 oelint-adv==9.11.2

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
