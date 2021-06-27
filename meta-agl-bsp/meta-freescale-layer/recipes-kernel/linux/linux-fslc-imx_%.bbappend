FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:${THISDIR}/files:"

require recipes-kernel/linux/linux-yocto-agl.inc

# These patches and the configuration fragment below will need to be
# revisited if/when using IMX_DEFAULT_BSP = "mainline" with i.MX8
# becomes more feasible with upstream meta-freescale.
SRC_URI_append_etnaviv = " \
    file://0001-enable-mhdp-with-etnaviv.patch \
    file://0002-dts-enable-etnaviv.patch \
"

# Make sure these are enabled so that AGL systemd configuration works
AGL_KCONFIG_FRAGMENTS += " \
    tmpfs.cfg \
    namespace.cfg \
    cgroup.cfg \
"

# Support for CFG80211 subsystem
AGL_KCONFIG_FRAGMENTS += "cfg80211.cfg"

# Turn off a couple of things enabled by default by Freescale
# (lock debugging and userspace firmware loader fallback)
AGL_KCONFIG_FRAGMENTS += "fixups.cfg"

# Support for i.MX8MQ EVKB (e.g. Broadcom wifi)
AGL_KCONFIG_FRAGMENTS_append_imx8mqevk = " imx8mq-evkb.cfg"

# Build in etnaviv if required
AGL_KCONFIG_FRAGMENTS_append_etnaviv = " etnaviv.cfg"
