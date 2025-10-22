SUMMARY = "AGL environment file for KUKSA.val databroker"
HOMEPAGE = "https://github.com/eclipse/kuksa.val"
BUGTRACKER = "https://github.com/eclipse/kuksa.val/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://kuksa-databroker.default \
           file://kuksa-databroker.open \
"

inherit allarch update-alternatives

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${WORKDIR}/kuksa-databroker.default ${D}${sysconfdir}/default/
    install -m 0644 ${WORKDIR}/kuksa-databroker.open ${D}${sysconfdir}/default/
}

ALTERNATIVE_LINK_NAME[kuksa-databroker-env] = "${sysconfdir}/default/kuksa-databroker"

RDEPENDS:${PN} += "kuksa-databroker kuksa-certificates-agl vss-agl"
#RPROVIDES:${PN} = "kuksa-databroker-env"
ALTERNATIVE:${PN} = "kuksa-databroker-env"
ALTERNATIVE_TARGET_${PN} = "${sysconfdir}/default/kuksa-databroker.default"

# Configuration that allows remote access
PACKAGE_BEFORE_PN += "${PN}-open"
FILES:${PN}-open += "${sysconfdir}/default/kuksa-databroker.open"
RDEPENDS:${PN}-open = "kuksa-databroker kuksa-certificates-agl vss-agl"
RPROVIDES:${PN}-open = "kuksa-databroker-env"
ALTERNATIVE:${PN}-open = "kuksa-databroker-env"
ALTERNATIVE_TARGET_${PN}-open = "${sysconfdir}/default/kuksa-databroker.open"
