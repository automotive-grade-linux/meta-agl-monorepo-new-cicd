SUMMARY     = "AGL Application Launcher service"
DESCRIPTION = "AGL Application Launcher service build with recipe method"
HOMEPAGE    = "https://git.automotivelinux.org/src/applaunchd"
SECTION     = "apps"
LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ae6497158920d9524cf208c09cc4c984"

DEPENDS = " \
    glib-2.0 \
    glib-2.0-native \
    systemd \
"

PV = "2.0+git${SRCPV}"

SRC_URI = " \
    git://gerrit.automotivelinux.org/gerrit/src/applaunchd;protocol=https;branch=${AGL_BRANCH}  \
    file://agl-app@.service \
    file://no-network.conf \
    file://private-tmp.conf \
"
SRCREV = "efbd734aca8b813710d7564d79696b1cf150a88c"

S = "${WORKDIR}/git"

inherit meson pkgconfig

do_install:append() {
    # Install generic template for all agl-app services
    mkdir -p ${D}${sysconfdir}/systemd/system/
    install -m 644 ${WORKDIR}/agl-app@.service ${D}${sysconfdir}/systemd/system/

    # Install individual sandboxing overrides/drop-ins to be used by apps
    mkdir -p ${D}${sysconfdir}/systemd/sandboxing/
    install -m 644 ${WORKDIR}/no-network.conf ${D}${sysconfdir}/systemd/sandboxing/
    install -m 644 ${WORKDIR}/private-tmp.conf ${D}${sysconfdir}/systemd/sandboxing/
}

FILES:${PN} += " ${datadir}/dbus-1/"

RDEPENDS:${PN} += " \
    agl-session \
    polkit-rule-agl-app \
"
