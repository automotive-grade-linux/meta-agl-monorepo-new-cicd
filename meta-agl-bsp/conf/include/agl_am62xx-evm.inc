# am62xx-evm has a k3r5 BBMULTICONFIG to build its bootloader
# firmware, need to use an override to set DEFAULTTUNE back to
# what that machine configuration needs.
DEFAULTTUNE:k3r5:forcedefaulttune = "armv7athf"

# meta-ti-bsp layer conf does break expectations wrt common folders
# meta-ti-bsp/conf/layer.conf:TI_COMMON_DEPLOY ?= "${TOPDIR}/deploy-ti"
TI_COMMON_DEPLOY := "${TMPDIR}/deploy"
