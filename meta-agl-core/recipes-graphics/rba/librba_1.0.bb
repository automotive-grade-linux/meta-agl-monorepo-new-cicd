SUMMARY = "RBA"
DESCRIPTION = "Rule Based Arbitrator decides which of the content to display \
when a large number of contents to be displayed on the cockpit display device \
(CID, meter, HUD, etc.) occur simultaneously under a certain rule (arbitration)"

SECTION = "libs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=13afa517927767fe9b7a65818a02bd8f"

INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

SRC_URI = "git://gerrit.automotivelinux.org/gerrit/staging/rba;protocol=https;branch=master"
SRCREV = "87c0278dfbcf0953330330c28a8d48636dd4df7e"

S = "${WORKDIR}/git"

inherit pkgconfig cmake

FILES_${PN} = "${libdir}/"
FILES_${PN}-dev = "${libdir}/pkgconfig/librba.pc \
                   ${includedir}/ \
                  "
INSANE_SKIP_${PN} += "dev-so"
INSANE_SKIP_${PN}-dev += "dev-elf"
