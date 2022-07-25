SUMMARY     = "System unit to relabel resolve.conf"
LICENSE     = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://resolv-conf-relabel.service"

inherit systemd allarch features_check

SYSTEMD_SERVICE:${PN} = "${BPN}.service"

REQUIRED_DISTRO_FEATURES = "systemd"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/resolv-conf-relabel.service ${D}${systemd_system_unitdir}/
}

FILES:${PN} += "${systemd_system_unitdir}"
