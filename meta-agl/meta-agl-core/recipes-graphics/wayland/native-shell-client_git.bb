SUMMARY = "Minimal agl-shell Wayland protocol client"

HOMEPAGE = "https://gerrit.automotivelinux.org/gerrit/q/project:src%252Fnative-shell-client"
SECTION = "x11"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=3b90ee643ce04400848a8f0deb492a4a"

DEPENDS = "wayland wayland-protocols wayland-native agl-compositor"

SRC_URI = "git://gerrit.automotivelinux.org/gerrit/src/native-shell-client.git;protocol=https;branch=${AGL_BRANCH}"
SRCREV = "c07e9511bd03e6f3416fd3be59b5720d73b68ef2"

PV = "0.0.1+git${SRCPV}"

inherit meson pkgconfig

# Reuse include file from upstream weston since we have the same requirements
require recipes-graphics/wayland/required-distro-features.inc
