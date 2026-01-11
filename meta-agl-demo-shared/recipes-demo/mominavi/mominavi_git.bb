SUMMARY     = "Momiyama navigation example based on mapbox."
DESCRIPTION = "The mominavi is a navigation example based on mapbox. It's based on aglqtnavigation. \
               The mominavi is not require agl-appfw."
LICENSE = "GPL-3.0-only"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d32239bcb673463ab874e80d47fae504"

DEPENDS = " \
    qtbase \
    qtdeclarative \
    qtwayland \
    qtlocation \
    qtsvg \
    qtwebsockets \
    "

PV = "2.0.0"

SRC_URI = "git://gerrit.automotivelinux.org/gerrit/apps/mominavi;protocol=https;branch=${AGL_BRANCH} \
           file://mominavi.service \
           file://mominavi \
          "
SRCREV = "1310154658b45a1d3d3685fb2efad62f40f2efe9"

S = "${WORKDIR}/git"

inherit qt6-qmake systemd

MOMIMAP_INITIAL_LATITUDE ??= "36.129"
MOMIMAP_INITIAL_LONGITUDE ??= "-115.1533"
QT_INSTALL_PREFIX = "/usr"

do_install:append() {
    install -d ${D}/${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/mominavi.service ${D}/${systemd_unitdir}/system

    install -m 0755 -d ${D}${sysconfdir}/default/
    install -m 0755 ${WORKDIR}/mominavi ${D}${sysconfdir}/default/

    echo 'MOMIMAP_INITIAL_LATITUDE=${MOMIMAP_INITIAL_LATITUDE}' >> ${D}${sysconfdir}/default/mominavi
    echo 'MOMIMAP_INITIAL_LONGITUDE=${MOMIMAP_INITIAL_LONGITUDE}' >> ${D}${sysconfdir}/default/mominavi
}

FILES:${PN} += " \
    ${systemd_unitdir} \
    ${sysconfdir}/*/* \
    "
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "mominavi.service"

RDEPENDS:${PN} = " \
    qtsvg qtsvg-plugins qtsvg-qmlplugins \
    qtwebsockets qtwebsockets-plugins qtwebsockets-qmlplugins \
    qtlocation qtlocation-plugins qtlocation-qmlplugins \
    maplibre-native-qt maplibre-native-qt-plugins maplibre-native-qt-qmlplugins \
    "
