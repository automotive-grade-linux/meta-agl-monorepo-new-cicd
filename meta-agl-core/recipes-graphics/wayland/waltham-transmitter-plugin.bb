DESCRIPTION = "Waltham transmitter is a libweston plug-in that adds support for receiving input events from a surface streamed on a remote output"
HOMEPAGE = "https://gerrit.automotivelinux.org/gerrit/src/waltham-transmitter-plugin.git"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=f21c9af4de068fb53b83f0b37d262ec3"

DEPENDS += "wayland wayland-native waltham weston"

SRC_URI = "git://gerrit.automotivelinux.org/gerrit/src/waltham-transmitter-plugin.git;protocol=https;branch=${AGL_BRANCH}"
AGL_BRANCH:aglnext = "next"
SRCREV = "d64b99a999fcc17322393782fe802122fd963ced"
SRCREV:aglnext = "${AUTOREV}"

S = "${WORKDIR}/git"

inherit meson pkgconfig python3native

FILES:${PN} += "${libdir}/*"
FILES:${PN} += "${bindir}/*"
