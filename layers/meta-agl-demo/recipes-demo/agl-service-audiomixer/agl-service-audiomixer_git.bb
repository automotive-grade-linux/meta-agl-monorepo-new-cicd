SUMMARY     = "Audio Mixer Service Daemon"
DESCRIPTION = "AGL Audio Mixer Service Daemon"
HOMEPAGE    = "https://gerrit.automotivelinux.org/gerrit/#/admin/projects/apps/agl-service-audiomixer"
SECTION     = "apps"
LICENSE     = "MIT & Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;beginline=3;md5=e8ad01a5182f2c1b3a2640e9ea268264"

DEPENDS = " \
    glib-2.0 \
    libtoml11 \
    openssl \
    systemd \
    pipewire \
    wireplumber \
    protobuf-native \
    grpc-native \
    protobuf \
    grpc \
    kuksa-databroker \
"

SRC_URI = "git://gerrit.automotivelinux.org/gerrit/apps/agl-service-audiomixer.git;protocol=https;branch=${AGL_BRANCH} \
           file://agl-service-audiomixer.token \
           file://kuksa.toml \
           file://agl-service-audiomixer.service \
           file://databroker.conf \
"
SRCREV  = "b51d14b84894a1ff8faa10183bd67470aeb75e8e"

PV = "2.0+git${SRCPV}"

inherit meson pkgconfig systemd

EXTRA_OEMESON += "-Dprotos=${STAGING_INCDIR}"

SYSTEMD_SERVICE:${PN} = "agl-service-audiomixer.service" 

do_install:append() {
    # VIS authorization token file for KUKSA.val should ideally not
    # be readable by other users, but currently that's not doable
    # until a packaging/sandboxing/MAC scheme is (re)implemented or
    # something like OAuth is plumbed in as an alternative.
    install -d ${D}${sysconfdir}/xdg/AGL/agl-service-audiomixer
    install -m 0644 ${UNPACKDIR}/kuksa.toml ${D}${sysconfdir}/xdg/AGL/agl-service-audiomixer/
    install -m 0644 ${UNPACKDIR}/agl-service-audiomixer.token ${D}${sysconfdir}/xdg/AGL/agl-service-audiomixer/

    # Replace the default systemd unit
    install -m 0644 ${UNPACKDIR}/agl-service-audiomixer.service ${D}${systemd_system_unitdir}/
    install -m 0644 -D ${UNPACKDIR}/databroker.conf ${D}${systemd_system_unitdir}/agl-service-audiomixer.d/databroker.conf
}

FILES:${PN} += "${systemd_system_unitdir}"

PACKAGE_BEFORE_PN += "${PN}-systemd-databroker"

FILES:${PN}-systemd-databroker += "${systemd_system_unitdir}/agl-service-audiomixer.d/databroker.conf"
