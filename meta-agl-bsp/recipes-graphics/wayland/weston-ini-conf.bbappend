FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI += "${@bb.utils.contains("DISTRO_FEATURES", "weston-remoting", "file://remote-output.cfg", "",d)}"
