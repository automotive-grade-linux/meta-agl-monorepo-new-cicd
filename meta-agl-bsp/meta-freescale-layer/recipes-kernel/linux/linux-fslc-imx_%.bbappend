FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

require linux-fslc.inc

# These patches and the configuration fragment below will need to be
# revisited if/when using IMX_DEFAULT_BSP = "mainline" with i.MX8
# becomes more feasible with upstream meta-freescale.
SRC_URI:append:etnaviv = " \
    file://0001-enable-mhdp-with-etnaviv.patch \
    file://0002-dts-enable-etnaviv.patch \
"

# Support for i.MX8MQ EVKB (e.g. Broadcom wifi)
AGL_KCONFIG_FRAGMENTS:append:imx8mq-evk = " imx8mq-evkb.cfg"

# Build in etnaviv if required
AGL_KCONFIG_FRAGMENTS:append:etnaviv = " etnaviv.cfg"
