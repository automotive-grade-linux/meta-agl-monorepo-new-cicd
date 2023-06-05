SUMMARY = "Provides a set of tools for development for AGL DISTRO"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} = "\
        strace \
        ldd \
        less \
        vim \
        lsof \
        gdb \
        valgrind \
        screen \
        usbutils \
        rsync \
        pstree \
        procps \
        libxslt-bin \
        gcc-sanitizers \
        pciutils \
        openssh-sftp-server \
        zstd \
"
