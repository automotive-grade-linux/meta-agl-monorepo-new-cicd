SUMMARY = "King fisher specific gpsd config"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/BSD-3-Clause;md5=550794465ba0ec5312d6919e203a55f9"

SRC_URI = " \
    file://gpsd.kingfisher \
"

inherit update-alternatives

RPROVIDES:${PN} += "virtual/gpsd-conf"

ALTERNATIVE:${PN} = "gpsd-defaults"
ALTERNATIVE_LINK_NAME[gpsd-defaults] = "${sysconfdir}/default/gpsd"
ALTERNATIVE_TARGET[gpsd-defaults] = "${sysconfdir}/default/gpsd.kingfisher"
ALTERNATIVE_PRIORITY[gpsd-defaults] = "20"

COMPATIBLE_MACHINE = "ulcb"
PACKAGE_ARCH = "${MACHINE_ARCH}"

do_install() {
    install -d ${D}/${sysconfdir}/default
    install -m 0644 ${WORKDIR}/gpsd.kingfisher ${D}/${sysconfdir}/default/gpsd.kingfisher
}

FILES:${PN} = "${sysconfdir}/default/gpsd.kingfisher"
CONFFILES:${PN} = "${sysconfdir}/default/gpsd.kingfisher"
