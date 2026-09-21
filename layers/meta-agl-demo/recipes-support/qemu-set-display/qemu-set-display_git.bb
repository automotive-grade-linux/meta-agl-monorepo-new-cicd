DESCRIPTION = "Helper tool to enable the secondandary vnc displays"
LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=361b6b837cad26c6900a926b62aada5f"


SRC_URI = "git://github.com/dhobsong/qemu-set-display.git;protocol=https;branch=main"
SRCREV = "31aa12acf0d760ab972507d5cdc2237840646822"

DEPENDS += "libvncserver"

inherit meson pkgconfig

PV = "0.1+git${SRCPV}"
