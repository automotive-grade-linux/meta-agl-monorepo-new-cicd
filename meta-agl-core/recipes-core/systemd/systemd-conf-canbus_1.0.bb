SUMMARY = "Systemd canbus configuration"
DESCRIPTION = "Systemd may require slightly different configuration for \
different machines. This injects configuration for the CAN bus."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PE = "1"

SRC_URI = "\
    file://canbus-can.network \
    file://canbus-can-fd.network \
    file://canbus-virtio.network \
    file://canbus-can.link \
"

CANBUS_NETWORK_CONFIG ??= "canbus-can.network"
CANBUS_NETWORK_CONFIG:virtio-all ?= "canbus-virtio.network"

do_install() {
    # Install CAN bus network configuration
    install -d ${D}${nonarch_base_libdir}/systemd/network/
    install -m 0644 ${WORKDIR}/${CANBUS_NETWORK_CONFIG} ${D}${nonarch_base_libdir}/systemd/network/60-canbus-can.network

    # Install link configuration to bump queue size on physical CAN bus devices
    install -m 0644 ${WORKDIR}/canbus-can.link ${D}${nonarch_base_libdir}/systemd/network/60-canbus-can.link
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

FILES:${PN} = " \
    ${nonarch_base_libdir}/systemd/network/ \
"
