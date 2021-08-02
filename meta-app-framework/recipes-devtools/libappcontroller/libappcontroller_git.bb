SUMMARY = "Controller library for an Application Framework"
DESCRIPTION = "Controller library to be used to easily create a binding for AGL App Framework"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "git://gerrit.automotivelinux.org/gerrit/src/libappcontroller;protocol=https;branch=${AGL_BRANCH}"
SRCREV = "1bef31c8bbad27f4914484c5007b2e199fb073d4"

PV = "${AGLVERSION}"
S  = "${WORKDIR}/git"

DEPENDS:append = " af-binder libafb-helpers lua"
RDEPENDS:${PN}:append = " af-binder lua"

inherit cmake

ALLOW_EMPTY:${PN} = "1"

