SUMMARY     = "Helper for enabling UART connected HCI Bluetooth devices"
LICENSE     = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit systemd allarch

SRC_URI = "file://hci-uart-helper.service \
           file://hci-uart-helper.sh \
"

COMPATIBLE_MACHINE = "imx8mqevk"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    # Install helper script
    install -d ${D}${sbindir}
    install -m 0755 ${WORKDIR}/hci-uart-helper.sh ${D}${sbindir}/

    # Install systemd unit
    install -d ${D}${systemd_system_unitdir}/
    install -m 0644 ${WORKDIR}/hci-uart-helper.service ${D}${systemd_system_unitdir}/
    install -d ${D}${systemd_system_unitdir}/bluetooth.service.wants
    ln -s ../hci-uart-helper.service ${D}${systemd_system_unitdir}/bluetooth.service.wants/
}

FILES_${PN} += "${systemd_system_unitdir}"

RDEPENDS_${PN} += "bluez5"
