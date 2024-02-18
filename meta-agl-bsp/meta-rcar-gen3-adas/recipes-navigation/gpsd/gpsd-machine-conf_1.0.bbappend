FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI = "file://gpsd.kingfisher"

inherit update-alternatives

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}/${sysconfdir}/default
    install -m 0644 ${WORKDIR}/gpsd.kingfisher ${D}/${sysconfdir}/default/gpsd.kingfisher
}

COMPATIBLE_MACHINE = "ulcb"
PACKAGE_ARCH = "${MACHINE_ARCH}"

CONFFILES:${PN} = "${sysconfdir}/default/gpsd.kingfisher"

ALTERNATIVE:${PN} = "gpsd-defaults"
ALTERNATIVE_LINK_NAME[gpsd-defaults] = "${sysconfdir}/default/gpsd"
ALTERNATIVE_TARGET[gpsd-defaults] = "${sysconfdir}/default/gpsd.kingfisher"
ALTERNATIVE_PRIORITY[gpsd-defaults] = "20"
