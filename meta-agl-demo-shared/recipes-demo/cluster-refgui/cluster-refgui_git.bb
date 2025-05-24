DESCRIPTION = "AGL Cluster Reference GUI"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5335066555b14d832335aa4660d6c376"

DEPENDS = " \
    qttools-native \
    qtmultimedia \
"
DEPENDS:append = " \
    ${@bb.utils.contains('AGL_FEATURES', 'agl-ic', 'cluster-service', '', d)} \
"

BRANCH = "master"
SRC_URI = "git://gerrit.automotivelinux.org/gerrit/src/cluster-refgui;protocol=https;branch=${BRANCH} \
           file://cluster.service \
           file://cluster \
"
SRCREV = "0e9a54fb74677c626d278473a588beab4431c101"

S = "${WORKDIR}/git"

inherit cmake qt6-cmake systemd pkgconfig

PACKAGECONFIG = "${@bb.utils.contains('AGL_FEATURES', 'agl-ic', '', 'disable-service', d)}"
PACKAGECONFIG[disable-service] = "-DDISABLE_CLUSTER_BACKEND=1"

SYSTEMD_SERVICE:${PN} = "cluster.service"

do_install:append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${WORKDIR}/cluster.service ${D}${systemd_unitdir}/system/
    
    install -m 0755 -d ${D}${sysconfdir}/default/
    install -m 0755 ${WORKDIR}/cluster ${D}${sysconfdir}/default/
}

FILES:${PN} += "/opt/apps/"

RDEPENDS:${PN} = " \
    qtbase \
    qtdeclarative \
    qt3d \
    qtmultimedia \
    qtwayland \
"
