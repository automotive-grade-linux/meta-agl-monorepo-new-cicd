DESCRIPTION = "AGL Xen test domu configuration file"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "file://domu-1.cfg.in"

S = "${UNPACKDIR}"

DOM_KERNEL_IMAGETYPE ?= "${KERNEL_IMAGETYPE}"
DOM_MACHINE ?= "${AGL_GUEST_MACHINE}"

# Assume partition from agl-xen-image-test configuration
DOM_PARTITION ?= "/dev/mmcblk0p4"

do_configure[noexec] = "1"

do_compile() {
    sed -e "s/@KERNEL@/${DOM_KERNEL_IMAGETYPE}-${DOM_MACHINE}.bin/g" \
        -e "s|@PARTITION@|${DOM_PARTITION}|g" \
	${UNPACKDIR}/domu-1.cfg.in > ${WORKDIR}/domu-1.cfg
}

do_install() {
    install -D -m 0644 ${WORKDIR}/domu-1.cfg ${D}${sysconfdir}/xen/auto/domu-1.cfg
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

RDEPENDS:${PN} = "xen-tools"
