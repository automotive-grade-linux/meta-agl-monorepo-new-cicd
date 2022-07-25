SUMMARY     = "System unit to relabel systemd generated files"
LICENSE     = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://systemd-selinux-relabel.service \
           file://systemd-selinux-relabel.sh \
"

inherit systemd allarch features_check

SYSTEMD_SERVICE:${PN} = "${BPN}.service"

REQUIRED_DISTRO_FEATURES = "systemd"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/systemd-selinux-relabel.service ${D}${systemd_system_unitdir}/
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/systemd-selinux-relabel.sh ${D}${sbindir}/
}

FILES:${PN} += "${systemd_system_unitdir}"
