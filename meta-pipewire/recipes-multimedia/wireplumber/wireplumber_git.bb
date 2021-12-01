SUMMARY     = "Session / Policy Manager for PipeWire"
HOMEPAGE    = "https://gitlab.freedesktop.org/pipewire/wireplumber"
BUGTRACKER  = "https://gitlab.freedesktop.org/pipewire/wireplumber/issues"
AUTHOR      = "George Kiagiadakis <george.kiagiadakis@collabora.com>"
SECTION     = "multimedia"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;beginline=3;md5=e8ad01a5182f2c1b3a2640e9ea268264"

inherit meson pkgconfig systemd

DEPENDS = "glib-2.0 glib-2.0-native pipewire lua"

SRC_URI = "\
    git://gitlab.freedesktop.org/pipewire/wireplumber.git;protocol=https;branch=master \
"
# v0.4.5
SRCREV = "3946457a7942a179c0f61c60de8cb8fc643391dd"

# patches to be able to compile with lower version of meson that is available in AGL.
SRC_URI += "\
    file://0001-Revert-wp-uninstalled-build-this-script-with-the-mes.patch \
    file://0002-Revert-tests-add-pipewire-env-variables-when-running.patch \
"

PV = "0.4.5"
S  = "${WORKDIR}/git"

WPAPI="0.4"

# use shared lua from the system instead of the static bundled one
EXTRA_OEMESON += "-Dsystem-lua=true"

# introspection in practice is only used for generating API docs
# API docs are available on the website and we don't need to build them
# (plus they depend on hotdoc which is not available here)
EXTRA_OEMESON += "-Dintrospection=disabled -Ddoc=disabled"

PACKAGECONFIG = "\
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
"

PACKAGECONFIG[systemd] = "-Dsystemd=enabled -Dsystemd-system-service=true -Dsystemd-user-service=false,-Dsystemd=disabled -Dsystemd-system-service=false -Dsystemd-user-service=false,systemd"

do_configure:prepend() {
    # relax meson version requirement
    # we only need 0.54 when building with -Dsystem-lua=false
    sed "s/meson_version : '>= 0.56.0'/meson_version : '>= 0.53.2'/" ${S}/meson.build > ${S}/tmp.build
    mv -f ${S}/tmp.build ${S}/meson.build
}

PACKAGES =+ "\
    lib${PN}-${WPAPI} \
    ${PN}-config \
"

SYSTEMD_SERVICE:${PN} = "wireplumber.service"
FILES:${PN} = "\
    ${bindir}/wireplumber \
    ${bindir}/wpctl \
    ${bindir}/wpexec \
    ${libdir}/wireplumber-${WPAPI}/* \
    ${datadir}/wireplumber/* \
    ${systemd_system_unitdir}/* \
"
RPROVIDES:${PN} += "virtual/pipewire-sessionmanager"
RDEPENDS:${PN} += "virtual/wireplumber-config"

FILES:lib${PN}-${WPAPI} = "\
    ${libdir}/libwireplumber-${WPAPI}.so.* \
"

FILES:${PN}-config += "\
    ${sysconfdir}/wireplumber/* \
"
CONFFILES:${PN}-config += "\
    ${sysconfdir}/wireplumber/* \
"
RPROVIDES:${PN}-config += "virtual/wireplumber-config"
