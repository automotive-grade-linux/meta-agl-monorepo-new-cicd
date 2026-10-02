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

# oelint-adv via pip, system-wide (not --user): once bitbake's build environment is sourced
# (init-build-env), PATH gets reset to a curated minimal set (oe-core/bitbake scripts +
# /usr/sbin:/usr/bin:/sbin:/bin) and HOME may be redirected - a --user install under
# ~/.local/bin would be invisible there. System-wide lands in /usr/local/bin, which survives
# the prune. Debian stable marks system Python as externally-managed (PEP 668);
# --break-system-packages is required.
#
# Pinned, not left to "whatever's latest at image-build time": validate.sh's recipe-lint
# check shouldn't silently vary by whichever version happened to be latest when someone
# (re)built this image - pinned to what this project has actually been validated against.
# (bitbake-setup needs no install: it ships inside the bitbake checkout that
# ci/scripts/_common.sh bootstraps at the revision pinned in kas/pins.yml.)
RUN python3 -m pip install --break-system-packages --no-cache-dir oelint-adv==9.11.2

RUN useradd --create-home --uid 1000 --shell /bin/bash ci
WORKDIR /home/ci

COPY ci/docker/entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

# agl_container() in ci/scripts/_common.sh starts the image as root and relies on the
# entrypoint to drop privileges (see entrypoint.sh) before exec'ing the requested command.
ENTRYPOINT ["/entrypoint.sh"]
CMD ["bash"]
