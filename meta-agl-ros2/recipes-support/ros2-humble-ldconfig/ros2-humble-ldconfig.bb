SUMMARY = "ROS2 runtime library setup"
DESCRIPTION = "Installs a configuration snippet for ROS 2 runtime libraries via ld.so.conf.d"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://ros2.conf"

do_install() {
    mkdir -p ${D}${sysconfdir}/ld.so.conf.d/
    install -m 644 ${WORKDIR}/ros2.conf ${D}${sysconfdir}/ld.so.conf.d/
}
