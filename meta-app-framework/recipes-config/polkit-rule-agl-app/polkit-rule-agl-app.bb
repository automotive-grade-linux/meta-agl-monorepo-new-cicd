SUMMARY = "Rule for agl-driver to control agl-app@ services"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

require recipes-extended/polkit/polkit-group-rule.inc

SRC_URI = "file://50-agl-app.rules"

S = "${UNPACKDIR}"

inherit allarch

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -m 0644 ${UNPACKDIR}/50-agl-app.rules ${D}${datadir}/polkit-1/rules.d/
}

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "-g 1003 applaunchd"
