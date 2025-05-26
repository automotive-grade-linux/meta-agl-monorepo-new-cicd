FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

require linux-fslc.inc

SRC_URI:append:etnaviv = " \
    file://0001-Enable-etnaviv-driver-on-i.MX8MQ-EVK.patch \
"

# Support for i.MX8MQ EVKB (e.g. Broadcom wifi)
AGL_KCONFIG_FRAGMENTS:append:imx8mq-evk = " imx8mq-evkb.cfg"

# Build in etnaviv if required
AGL_KCONFIG_FRAGMENTS:append:etnaviv = " etnaviv.cfg"
