FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

require recipes-kernel/linux/linux-yocto-agl.inc

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

do_install:append:cubox-i() {
    # Add symlink to work with default Hummingboard 2 u-boot configuration
    ln -sf imx6q-hummingboard2.dtb ${D}/boot/imx6q-hummingboard2-emmc.dtb
}
