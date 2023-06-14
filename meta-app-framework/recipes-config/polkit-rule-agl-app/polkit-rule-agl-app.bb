SUMMARY = "Rule for agl-driver to control agl-app@ services"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://50-agl-app.rules"

DEPENDS += "polkit"

inherit features_check
REQUIRED_DISTRO_FEATURES = "polkit"

do_install() {
    install -m 700 -d ${D}${datadir}/polkit-1/rules.d
    chown polkitd:root ${D}/${datadir}/polkit-1/rules.d
    install -m 0644 ${WORKDIR}/50-agl-app.rules ${D}${datadir}/polkit-1/rules.d
}

FILES:${PN} += "${datadir}/polkit-1/rules.d/*.rules"
