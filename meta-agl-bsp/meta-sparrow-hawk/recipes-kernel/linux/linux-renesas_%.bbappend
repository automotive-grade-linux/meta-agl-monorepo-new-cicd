FILESEXTRAPATHS:prepend := "${THISDIR}/linux-renesas:"

require recipes-kernel/linux/linux-yocto-agl.inc

SRC_URI:append:sparrow-hawk = " \
    file://0001-Add-Olimex-MIPI-HDMI-adapter-support.patch \
"

KERNEL_DEVICETREE:append:sparrow-hawk = " \
    renesas/r8a779g3-sparrow-hawk-dsi-mipi-hmdi.dtbo \
"

AGL_KCONFIG_FRAGMENTS:append:sparrow-hawk = " lt8912b.cfg"
