SUMMARY = "A simple ncurses interface for connman"
DESCRIPTION = "A simple ncurses interface for connman"
HOMEPAGE = "https://gitlab.com/iotbzh/connman-json-client"

SECTION = "console/network"

DEPENDS = "dbus ncurses connman json-c"

LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c16666ae6c159876a0ba63099614381"

SRC_URI = "git://gitlab.com/iotbzh/connman-json-client.git;protocol=https;branch=master \
           file://stdbool-fixes.patch \
           file://0001-Do-not-overwrite-CFLAGS.patch \
           file://0001-Initialize-variables.patch \
           "
SRCREV = "2b0f93ec9518c978c04807fe52e95315d6d80e6b"

inherit autotools pkgconfig

EXTRA_AUTORECONF += " -vfi"

S = "${WORKDIR}/git"

do_install () {
    install -dm755 ${D}${bindir}
    install -Dm755 connman_ncurses ${D}${bindir}
}

FILES:${PN} = "${bindir}/connman_ncurses"
