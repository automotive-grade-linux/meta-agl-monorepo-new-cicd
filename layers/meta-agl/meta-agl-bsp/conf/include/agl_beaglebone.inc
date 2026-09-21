# This config is for the beaglebone (inspired from the vayu config).

# DEFAULTTUNE for cortex-a8
DISTRO_FEATURES:append = " agl-medium-arm-compiler"

WKS_FILE = "sdimage-bootpart-uuid.wks"

# meta-ti-bsp layer conf does break expectations wrt common folders
# meta-ti-bsp/conf/layer.conf:TI_COMMON_DEPLOY ?= "${TOPDIR}/deploy-ti"
TI_COMMON_DEPLOY := "${TMPDIR}/deploy"