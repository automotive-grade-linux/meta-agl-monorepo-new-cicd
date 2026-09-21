FILESEXTRAPATHS:prepend := "${THISDIR}/agl-compositor-init:"

SUMMARY = "Startup systemd unit for the AGL Wayland compositor with starting in the same time the DRM and PipeWire backends"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit systemd

# Reuse include file from upstream weston since we have the same requirements
require recipes-graphics/wayland/required-distro-features.inc

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "file://agl-compositor-pipewire.conf \
           file://agl-compositor-stream-pipewire.service \
"

S = "${UNPACKDIR}"

AGL_KVM_REMOTE_OUTPUT_IP ?= "172.16.10.3"
AGL_KVM_REMOTE_OUTPUT_PORT ?= "5005"

do_install() {
    sed -i -e "s,@REMOTE_OUTPUT_IP@,${AGL_KVM_REMOTE_OUTPUT_IP},g" \
	${UNPACKDIR}/agl-compositor-stream-pipewire.service

    sed -i -e "s,@REMOTE_OUTPUT_PORT@,${AGL_KVM_REMOTE_OUTPUT_PORT},g" \
	${UNPACKDIR}/agl-compositor-stream-pipewire.service

    install -D -p -m0644 ${UNPACKDIR}/agl-compositor-stream-pipewire.service ${D}${systemd_system_unitdir}/agl-compositor-stream-pipewire.service

    install -d ${D}${systemd_system_unitdir}/agl-compositor.service.d
    install -m644 ${UNPACKDIR}/agl-compositor-pipewire.conf ${D}/${systemd_system_unitdir}/agl-compositor.service.d/02-agl-compositor.conf
}

FILES:${PN} += "\
    ${systemd_system_unitdir}/agl-compositor.service.d \
    ${systemd_system_unitdir}/agl-compositor.service.d/02-agl-compositor.conf \
    ${systemd_system_unitdir}/agl-compositor-stream-pipewire.service \
    "

RDEPENDS:${PN} = "agl-compositor-init weston-ini"
RCONFLICTS:${PN} = "weston-init"

SYSTEMD_SERVICE:${PN} = "agl-compositor-stream-pipewire.service"
