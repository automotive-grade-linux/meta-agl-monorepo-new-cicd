SUMMARY = "The cluster service for AGL Instrument Cluster."
DESCRIPTION = "\
    The cluster-service is a common implementation for functional service \
    of cluster.  It aim to use reusable implementation for inter process \
    communication and process design. "
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ae6497158920d9524cf208c09cc4c984"

DEPENDS = "systemd alsa-lib"
PV = "1.0.0+rev${SRCREV}"

SRCREV = "d1cf2d48cbbb7a1d0b0739190c616be01f9612e1"
SRC_URI = " \
    git://github.com/agl-ic-eg/cluster-service.git;branch=main;protocol=https \
    file://cluster-service.service \
    "
S = "${WORKDIR}/git"

inherit autotools pkgconfig systemd

PACKAGECONFIG ?= "can fake"
PACKAGECONFIG[can] = "--enable-socketcan-data-source"
PACKAGECONFIG[fake] = "--enable-fake-data-source"

SYSTEMD_SERVICE:${PN} = "cluster-service.service"

do_install:append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/cluster-service.service ${D}${systemd_unitdir}/system/
}
