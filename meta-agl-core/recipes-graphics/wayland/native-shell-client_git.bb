SUMMARY = "Minimal agl-shell Wayland protocol client"

HOMEPAGE = "https://gerrit.automotivelinux.org/gerrit/q/project:src%252Fnative-shell-client"
SECTION = "x11"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b90ee643ce04400848a8f0deb492a4a"

DEPENDS = "wayland wayland-protocols wayland-native agl-compositor"

SRC_URI = "git://gerrit.automotivelinux.org/gerrit/src/native-shell-client.git;protocol=https;branch=${AGL_BRANCH}"
SRCREV = "03249cee92017337b1f2515b98a8f3b37459a256"

PV = "0.0.1+git${SRCPV}"
S = "${WORKDIR}/git"

inherit meson pkgconfig
