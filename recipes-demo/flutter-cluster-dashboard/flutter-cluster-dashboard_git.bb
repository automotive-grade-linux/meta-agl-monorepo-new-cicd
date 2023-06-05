SUMMARY = "Flutter Instrument Cluster "
DESCRIPTION = "An instrument cluster app written in dart for the flutter runtime"
AUTHOR = "Aakash Solanki"
HOMEPAGE = "https://gerrit.automotivelinux.org/gerrit/apps/flutter-instrument-cluster"

SECTION = "graphics"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=0c52b0e4b5f0dbf57ea7d44bebb2e29d"

SRC_URI = "git://gerrit.automotivelinux.org/gerrit/apps/flutter-instrument-cluster;protocol=https;branch=${AGL_BRANCH} \
    file://flutter-cluster-dashboard.service \
    file://flutter_cluster_dashboard_on_bg.json \
    file://flutter-cluster-dashboard.yaml \
    file://flutter-cluster-dashboard.yaml.demo \
"

PV = "1.0+git${SRCPV}"
SRCREV = "9bc83e64c508ad8c69a3950d5421774f9b53a31f"

S = "${WORKDIR}/git"

PUBSPEC_APPNAME = "flutter_cluster_dashboard"

FLUTTER_APPLICATION_INSTALL_PREFIX = "/flutter"

OPENROUTE_API_KEY ??= "YOU_NEED_TO_SET_IT_IN_LOCAL_CONF"

inherit flutter-app update-alternatives systemd

CLUSTER_DEMO_VISS_HOSTNAME ??= "192.168.10.2"

APP_CONFIG = "flutter_cluster_dashboard_on_bg.json"

SYSTEMD_SERVICE:${PN} = "flutter-cluster-dashboard.service"

do_install:append() {
    install -D -m 0644 ${WORKDIR}/${BPN}.service ${D}${systemd_system_unitdir}/${BPN}.service

    install -D -m 0644 ${WORKDIR}/${APP_CONFIG} ${D}${datadir}/flutter/${BPN}.json

    install -d ${D}${sysconfdir}/xdg/AGL
    install -m 0644 ${WORKDIR}/${BPN}.yaml ${D}${sysconfdir}/xdg/AGL/${BPN}.yaml.default
    install -m 0644 ${WORKDIR}/${BPN}.yaml.demo ${D}${sysconfdir}/xdg/AGL/
    sed -i "s/^hostname: .*/hostname: ${CLUSTER_DEMO_VISS_HOSTNAME}/" ${D}${sysconfdir}/xdg/AGL/${BPN}.yaml.demo

    install -m 0755 -d ${D}${sysconfdir}/default/ 
    echo 'OPENROUTE_API_KEY:${OPENROUTE_API_KEY}' >> ${D}${sysconfdir}/default/openroutekey
}

ALTERNATIVE_LINK_NAME[flutter-cluster-dashboard.yaml] = "${sysconfdir}/xdg/AGL/${BPN}.yaml"

FILES:${PN} += "${datadir} ${sysconfdir}/default/"

RDEPENDS:${PN} += "flutter-auto agl-flutter-env liberation-fonts"

PACKAGE_BEFORE_PN += "${PN}-conf ${PN}-conf-demo"

FILES:${PN}-conf += "${sysconfdir}/xdg/AGL/${BPN}.yaml.default"
RDEPENDS:${PN}-conf = "${PN}"
RPROVIDES:${PN}-conf = "${BPN}.yaml"
RCONFLICTS:${PN}-conf = "${PN}-conf-demo"
ALTERNATIVE:${PN}-conf = "${BPN}.yaml"
ALTERNATIVE_TARGET_${PN}-conf = "${sysconfdir}/xdg/AGL/${BPN}.yaml.default"

FILES:${PN}-conf-demo += "${sysconfdir}/xdg/AGL/${BPN}.yaml.demo"
RDEPENDS:${PN}-conf-demo = "${PN}"
RPROVIDES:${PN}-conf-demo = "${BPN}.yaml"
RCONFLICTS:${PN}-conf-demo = "${PN}-conf"
ALTERNATIVE:${PN}-conf-demo = "${BPN}.yaml"
ALTERNATIVE_TARGET_${PN}-conf-demo = "${sysconfdir}/xdg/AGL/${BPN}.yaml.demo"
