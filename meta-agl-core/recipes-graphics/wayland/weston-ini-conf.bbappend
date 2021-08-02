FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGE_ARCH = "${MACHINE_ARCH}"

WESTON_DISPLAYS:append = "${@bb.utils.contains("DISTRO_FEATURES", "weston-remoting", " remote-output", "", d)}"
WESTON_DISPLAYS:append = "${@bb.utils.contains("AGL_FEATURES", "waltham-remoting", " transmitter-output", "", d)}"

# For virtual machines and intel-corei7-64 we want to support both the HDMI-A-1
# and Virtual-1 outputs. This allows us to run virtual images on real hardware
# and vice versa.
WESTON_DISPLAYS:append:qemuall = " virtual-270"
WESTON_DISPLAYS:append:intel-corei7-64 = " virtual-270"
