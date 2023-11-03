# j721e-evm has a k3r5 BBMULTICONFIG to build its bootloader
# firmware, need to use an override to set DEFAULTTUNE back to
# what that machine configuration needs.
DEFAULTTUNE:k3r5 = "armv7athf"

# Workaround for recipe assumption of poky location
LIC_FILES_CHKSUM:pn-ti-rtos-firmware = "file://${THISDIR}/../../licenses/TI-TFL;md5=a1b59cb7ba626b9dbbcbf00f3fbc438a"

# meta-ti-bsp layer conf does break expectations wrt common folders
# meta-ti-bsp/conf/layer.conf:TI_COMMON_DEPLOY ?= "${TOPDIR}/deploy-ti"
TI_COMMON_DEPLOY := "${TMPDIR}/deploy"