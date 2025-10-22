SUMMARY     = "AGL Reference Navigation Cluster Streaming application"
DESCRIPTION = "Demo AGL turn by turn cluster navigation application based on QtLocation widget."
HOMEPAGE    = "https://gerrit.automotivelinux.org/gerrit/admin/repos/apps/tbtnavi"
SECTION     = "apps"

LICENSE = "Apache-2.0 & ISC & BSD-3-Clause & BSL-1.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ae6497158920d9524cf208c09cc4c984 \
                    file://LICENSE.mapbox-cheap-ruler-cpp;md5=761263ee6bdc98e8697d9fbc897021ba \
                    file://LICENSE.mapbox-geometry.hpp;md5=6e44f5d6aeec54f40fc84eebe3c6fc6c \
                    file://LICENSE.mapbox-variant;md5=79558839a9db3e807e4ae6f8cd100c1c \
                    file://include/mapbox/recursive_wrapper.hpp;beginline=4;endline=13;md5=cd3341aae76c0cf8345935abd20f0051 \
"

DEPENDS = " \
    qtbase \
    qtdeclarative \
    qtlocation \
    libqtappfw \
    qtwayland-native \
    protobuf \
    grpc \
    grpc-native \
"

PV = "2.0+git${SRCPV}"

SRC_URI = "git://gerrit.automotivelinux.org/gerrit/apps/tbtnavi;protocol=https;branch=${AGL_BRANCH} \
           file://tbtnavi.service \
           file://kuksa.toml \
           file://tbtnavi.token \
           file://kvm.conf \
"
SRCREV = "a89eac8f101fd6cd88b4a93dee02de03dab36c21"

S = "${WORKDIR}/git"

inherit meson systemd pkgconfig

SYSTEMD_SERVICE:${PN} = "${BPN}.service"

do_install:append() {
    install -D -m 0644 ${WORKDIR}/${BPN}.service ${D}${systemd_system_unitdir}/${BPN}.service

    install -D -m 0644 ${WORKDIR}/kvm.conf ${D}${systemd_system_unitdir}/${BPN}.service.d/kvm.conf

    # VIS authorization token file for KUKSA.val should ideally not
    # be readable by other users, but currently that's not doable
    # until a packaging/sandboxing/MAC scheme is (re)implemented or
    # something like OAuth is plumbed in as an alternative.
    install -d ${D}${sysconfdir}/xdg/AGL/tbtnavi
    install -m 0644 ${WORKDIR}/kuksa.toml ${D}${sysconfdir}/xdg/AGL/tbtnavi/
    install -m 0644 ${WORKDIR}/tbtnavi.token ${D}${sysconfdir}/xdg/AGL/tbtnavi/
}

RDEPENDS:${PN} += " \
    qtwayland \
    qtbase-qmlplugins \
    qt5compat \
    qtlocation \
    ondemandnavi-config \
    libqtappfw \
"

PACKAGE_BEFORE_PN += "${PN}-conf-kvm"

FILES:${PN}-conf-kvm += " \
    ${systemd_system_unitdir}/tbtnavi.service.d/kvm.conf \
"
