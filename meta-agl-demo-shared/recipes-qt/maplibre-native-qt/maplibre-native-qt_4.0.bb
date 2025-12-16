SUMMARY     = "MapLibre Native Qt bindings and Qt Location MapLibre Plugin."
DESCRIPTION = "Qt bindings for MapLibre GL Native - a library for embedding interactive, customizable vector maps"
HOMEPAGE = "https://github.com/maplibre/maplibre-native-qt"
LICENSE = "BSD-2-Clause & (GPL-2.0-only | GPL-3.0-only | LGPL-3.0-only)"
LIC_FILES_CHKSUM = "file://README.md;md5=2910c2703e8f25de11ec2bc02c01c912"

DEPENDS = " \
    qttools-native \
    qtbase \
    qtdeclarative \
    qtlocation \
    "

SRC_URI = " \
    gitsm://github.com/maplibre/maplibre-native-qt.git;protocol=https;branch=main \
    file://0001-Fix-installation-path-for-the-native-linux-environme.patch\
"
SRCREV = "181f28b8d147d10b9160e106694fbca811c911b9"

S = "${WORKDIR}/git"

inherit qt6-cmake pkgconfig

EXTRA_OECMAKE += "-DMLN_WITH_OPENGL=ON"

CXXFLAGS:append = " -Wno-error=shadow"

INSANE_SKIP:${PN}-dbg += "libdir"

SYSROOT_DIRS += "${QT6_INSTALL_BINDIR} ${QT6_INSTALL_LIBEXECDIR}"

PACKAGE_BEFORE_PN = "${PN}-qmlplugins ${PN}-plugins"

RRECOMMENDS:${PN} = " \
    ${PN}-plugins \
    ${PN}-qmlplugins \
"
RRECOMMENDS:${PN}:class-native = ""

RRECOMMENDS:${PN}-dev += " \
    ${PN}-staticdev \
"

FILES:${PN}-qmlplugins = " \
    ${QT6_INSTALL_QMLDIR} \
"

FILES:${PN}-plugins = " \
    ${QT6_INSTALL_PLUGINSDIR}/*/*${SOLIBSDEV} \
    ${QT6_INSTALL_PLUGINSDIR}/*/*/*${SOLIBSDEV} \
    ${QT6_INSTALL_PLUGINSDIR}/*/*/*/*${SOLIBSDEV} \
"

FILES:${PN} += " \
    ${QT6_INSTALL_LIBDIR}/lib*${SOLIBS} \
"

FILES:${PN}-dev += " \
    ${QT6_INSTALL_DESCRIPTIONSDIR} \
    ${QT6_INSTALL_DOCDIR} \
    ${QT6_INSTALL_INCLUDEDIR} \
    ${QT6_INSTALL_LIBDIR}/lib*${SOLIBSDEV} \
    ${QT6_INSTALL_LIBDIR}/*.prl \
    ${QT6_INSTALL_LIBDIR}/*.la \
    ${QT6_INSTALL_LIBDIR}/cmake \
    ${QT6_INSTALL_LIBDIR}/metatypes \
    ${QT6_INSTALL_LIBDIR}/pkgconfig \
    ${QT6_INSTALL_MKSPECSDIR} \
    ${QT6_INSTALL_QMLDIR}/*.qmltypes \
    ${QT6_INSTALL_QMLDIR}/*/*.qmltypes \
    ${QT6_INSTALL_QMLDIR}/*/*/*.qmltypes \
    ${QT6_INSTALL_QMLDIR}/*/*/*/*.qmltypes \
    ${QT6_INSTALL_QMLDIR}/*/*/*/*/*.qmltypes \
    ${QT6_INSTALL_QMLDIR}/*/designer \
    ${QT6_INSTALL_QMLDIR}/*/*/designer \
    ${QT6_INSTALL_QMLDIR}/*/*/*/designer \
"

FILES:${PN}-staticdev += " \
    ${QT6_INSTALL_LIBDIR}/*.a \
    ${QT6_INSTALL_PLUGINSDIR}/*/*.a \
    ${QT6_INSTALL_PLUGINSDIR}/*/*.prl \
    ${QT6_INSTALL_PLUGINSDIR}/*/*/*.a \
    ${QT6_INSTALL_PLUGINSDIR}/*/*/*.prl \
    ${QT6_INSTALL_QMLDIR}/*/*.a \
    ${QT6_INSTALL_QMLDIR}/*/*.prl \
    ${QT6_INSTALL_QMLDIR}/*/*/*.a \
    ${QT6_INSTALL_QMLDIR}/*/*/*.prl \
    ${QT6_INSTALL_QMLDIR}/*/*/*/*.a \
    ${QT6_INSTALL_QMLDIR}/*/*/*/*.prl \
    ${QT6_INSTALL_QMLDIR}/*/*/*/*/*.a \
    ${QT6_INSTALL_QMLDIR}/*/*/*/*/*.prl \
    ${QT6_INSTALL_LIBDIR}/objects* \
    ${QT6_INSTALL_PLUGINSDIR}/*/objects* \
    ${QT6_INSTALL_QMLDIR}/*/objects*/ \
    ${QT6_INSTALL_QMLDIR}/*/*/objects*/ \
    ${QT6_INSTALL_QMLDIR}/*/*/*/objects*/ \
    ${QT6_INSTALL_QMLDIR}/*/*/*/*/objects*/ \
"
