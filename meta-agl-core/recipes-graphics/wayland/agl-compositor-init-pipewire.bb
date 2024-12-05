FILESEXTRAPATHS:prepend := "${THISDIR}/agl-compositor-init:"

SUMMARY = "Startup systemd unit for the AGL Wayland compositor with starting in the same time the DRM and PipeWire backends"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "file://agl-compositor-pipewire.conf \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${systemd_system_unitdir}/agl-compositor.service.d
    install -m644 ${WORKDIR}/agl-compositor-pipewire.conf ${D}/${systemd_system_unitdir}/agl-compositor.service.d/02-agl-compositor.conf
}
FILES:${PN} += "\
    ${systemd_system_unitdir}/agl-compositor.service.d \
    ${systemd_system_unitdir}/agl-compositor.service.d/02-agl-compositor.conf \
    ${systemd_system_unitdir}/agl-compositor-stream-pipewire.service \
    "

RDEPENDS:${PN} = "agl-compositor-init weston-ini"
RCONFLICTS:${PN} = "weston-init"
