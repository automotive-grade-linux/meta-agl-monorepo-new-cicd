Overview
========

The
[AGL Project](https://www.automotivelinux.org/) is an automotive-specific
development environment that provides a Linux distribution
[AGL UCB](https://www.automotivelinux.org/software/unified-code-base).

AGL uses layers designed to be compatible with the
[Yocto Project](https://www.yoctoproject.org) and the
[OpenEmbedded Project (OE)](https://www.openembedded.org/wiki/Main_Page).

This section provides information about the layers used by the AGL Project:

* **`meta-agl/meta-agl-core`**: Minimal set of software needed to create an AGL distribution
  used to boot a system.
  AGL profiles are built on top of this minimal set of software.

    ```
    $ git clone https://gerrit.automotivelinux.org/gerrit/AGL/meta-agl
    ```

Maintenance
-----------

All patches must be submitted via the AGL Gerrit instance at
<https://gerrit.automotivelinux.org>.  See this wiki page for
details:

<https://wiki.automotivelinux.org/agl-distro/contributing>

Layer maintainers:
* Jan-Simon Möller <jsmoeller@linuxfoundation.org>
