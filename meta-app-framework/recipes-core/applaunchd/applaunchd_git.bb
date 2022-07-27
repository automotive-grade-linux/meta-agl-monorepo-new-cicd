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
    file://agl-app-web@.service \
    file://no-network.conf \
    file://private-tmp.conf \
"
SRCREV = "c675bafdf15cc19276bd8276c34f56404a5ecb62"

S = "${WORKDIR}/git"

inherit meson pkgconfig

do_install:append() {
    # Install generic template for all agl-app services
    install -d ${D}${systemd_system_unitdir}
    install -m 644 ${WORKDIR}/agl-app@.service ${D}${systemd_system_unitdir}/
    install -m 644 ${WORKDIR}/agl-app-web@.service ${D}${systemd_system_unitdir}/

    # Install individual sandboxing overrides/drop-ins to be used by apps
    install -d ${D}${systemd_system_unitdir}/sandboxing
    install -m 644 ${WORKDIR}/no-network.conf ${D}${systemd_system_unitdir}/sandboxing/
    install -m 644 ${WORKDIR}/private-tmp.conf ${D}${systemd_system_unitdir}/sandboxing/
}

PACKAGE_BEFORE_PN += "${PN}-template-agl-app ${PN}-template-agl-app-web"

FILES:${PN} += "${systemd_system_unitdir} ${datadir}/dbus-1/"

FILES:${PN}-template-agl-app = "${systemd_system_unitdir}/agl-app@.service"

FILES:${PN}-template-agl-app-web = "${systemd_system_unitdir}/agl-app-web@.service"

RDEPENDS:${PN} += " \
    agl-session \
    polkit-rule-agl-app \
"

RDEPENDS:${PN}-template-agl-app = "${PN}"

RDEPENDS:${PN}-template-agl-app-web = "${PN}"
