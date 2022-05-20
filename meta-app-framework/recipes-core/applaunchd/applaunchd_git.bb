SUMMARY     = "AGL Application Launcher service"
DESCRIPTION = "AGL Application Launcher service build with recipe method"
HOMEPAGE    = "https://git.automotivelinux.org/src/applaunchd"
SECTION     = "apps"
LICENSE     = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ae6497158920d9524cf208c09cc4c984"

DEPENDS = " \
    glib-2.0 \
    glib-2.0-native \
"

PV      = "1.0+git${SRCPV}"

SRC_URI = " \
        git://gerrit.automotivelinux.org/gerrit/src/applaunchd;protocol=https;branch=${AGL_BRANCH}  \
        "
SRCREV = "${AGL_APP_REVISION}"

S       = "${WORKDIR}/git"

inherit meson pkgconfig

FILES:${PN} += " ${datadir}/dbus-1/"

RDEPENDS:${PN} += " \
    agl-session \
"
