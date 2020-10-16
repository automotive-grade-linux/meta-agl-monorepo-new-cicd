DESCRIPTION = "Waltham transmitter is a libweston plug-in that adds support for receiving input events from a surface streamed on a remote output"
HOMEPAGE = "https://gerrit.automotivelinux.org/gerrit/src/waltham-transmitter-plugin.git"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f21c9af4de068fb53b83f0b37d262ec3"

DEPENDS += "wayland wayland-native waltham weston"

SRC_URI = "git://gerrit.automotivelinux.org/gerrit/src/waltham-transmitter-plugin.git;protocol=https;branch=${AGL_BRANCH}"
SRCREV = "c9d23a045e6cb46c023c15f8189ef3ee8b1ddf20"

S = "${WORKDIR}/git/"

inherit meson pkgconfig python3native

FILES_${PN} += "${libdir}/*"
FILES_${PN} += "${bindir}/*"
