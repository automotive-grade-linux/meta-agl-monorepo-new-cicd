FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI += "${SOURCEFORGE_MIRROR}/freetype/freetype-demos/${PV}/ft2demos-${PV}.tar.gz;name=ft2demos \
            file://0001-Makefile-dont-build-gfx-demos.patch;patchdir=../ft2demos-${PV} \
           "
SRC_URI[ft2demos.sha256sum] = "95939c04b72872f2c6053a436b385c614530322bda7a7966efbc4d281f710af6"

# Enable pixmap/libpng support to allow color emojis
PACKAGECONFIG:append = " pixmap"

do_compile:append () {
    oe_runmake -C ${B} FT2DEMOS=1 TOP_DIR_2=${WORKDIR}/ft2demos-${PV}
}

do_install:append () {
    install -d -m 0755 ${D}${bindir}
    for x in ftbench ftdump ftlint ftvalid ttdebug; do
        install -m 0755 ${WORKDIR}/ft2demos-${PV}/bin/.libs/$x ${D}${bindir}
    done
}

PACKAGE_BEFORE_PN = "${PN}-demos"

FILES:${PN}-demos = "\
    ${bindir}/ftbench \
    ${bindir}/ftdump \
    ${bindir}/ftlint \
    ${bindir}/ftvalid \
    ${bindir}/ttdebug \
"
