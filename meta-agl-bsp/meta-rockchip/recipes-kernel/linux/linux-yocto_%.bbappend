FILESEXTRAPATHS:prepend:rk3588s := "${THISDIR}/files:"

SRC_URI:append:rk3588s = " \
    file://0001-Add-can-to-dtb.patch \
    file://0002-Enable-can1-interface.patch \
"

AGL_KCONFIG_FRAGMENTS:append:rk3588s = " rockchip-sound.cfg rockchip-can.cfg"
