SUMMARY     = "Demo HVAC Service Daemon"
DESCRIPTION = "Demo HVAC Service Daemon"
HOMEPAGE    = "https://gerrit.automotivelinux.org/gerrit/#/admin/projects/apps/agl-service-hvac"

LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ae6497158920d9524cf208c09cc4c984"

DEPENDS = " \
    glib-2.0 \
    libtoml11 \
    openssl \
    systemd \
    protobuf-native \
    grpc-native \
    protobuf \
    grpc \
    kuksa-databroker \
"

SRC_URI = "git://gerrit.automotivelinux.org/gerrit/apps/agl-service-hvac;protocol=https;branch=${AGL_BRANCH} \
           file://agl-service-hvac.token \
           file://kuksa.toml \
           file://agl-service-hvac.service \
           file://databroker.conf \
"
SRCREV  = "20d83041ffcdafc325cb63e6de1ae5c42021cd0a"

PV = "2.0+git${SRCPV}"

inherit meson pkgconfig systemd

EXTRA_OEMESON += "-Dprotos=${STAGING_INCDIR}"

SYSTEMD_SERVICE:${PN} = "agl-service-hvac.service"

do_install:append() {
    # VIS authorization token file for KUKSA.val should ideally not
    # be readable by other users, but currently that's not doable
    # until a packaging/sandboxing/MAC scheme is (re)implemented or
    # something like OAuth is plumbed in as an alternative.
    install -d ${D}${sysconfdir}/xdg/AGL/agl-service-hvac
    install -m 0644 ${UNPACKDIR}/kuksa.toml ${D}${sysconfdir}/xdg/AGL/agl-service-hvac/
    install -m 0644 ${UNPACKDIR}/agl-service-hvac.token ${D}${sysconfdir}/xdg/AGL/agl-service-hvac/

    # Replace the default systemd unit
    install -m 0644 ${UNPACKDIR}/agl-service-hvac.service ${D}${systemd_system_unitdir}/
    install -m 0644 -D ${UNPACKDIR}/databroker.conf ${D}${systemd_system_unitdir}/agl-service-hvac.d/databroker.conf
}

FILES:${PN} += "${systemd_system_unitdir}"

PACKAGE_BEFORE_PN += "${PN}-systemd-databroker"

FILES:${PN}-systemd-databroker += "${systemd_system_unitdir}/agl-service-hvac.d/databroker.conf"

