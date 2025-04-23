FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

require recipes-kernel/linux/linux-yocto-agl.inc

SRC_URI += "file://0001-mconf-menuconfig.patch"

# Enable support for Pi foundation touchscreen
AGL_KCONFIG_FRAGMENTS += "raspberrypi-panel.cfg"

# Enable bt hci uart
AGL_KCONFIG_FRAGMENTS += "raspberrypi-hciuart.cfg"

# ENABLE NETWORK (built-in)
AGL_KCONFIG_FRAGMENTS += "raspberrypi_network.cfg"

CMDLINE_DEBUG = ""

CMDLINE:append = " usbhid.mousepoll=0"

# Add options to allow CMA to operate
CMDLINE:append = '${@oe.utils.conditional("ENABLE_CMA", "1", " coherent_pool=6M smsc95xx.turbo_mode=N", "", d)}'

KERNEL_MODULE_AUTOLOAD += "snd-bcm2835"
KERNEL_MODULE_AUTOLOAD += "hid-multitouch"

PACKAGES += "kernel-module-snd-bcm2835"

RDEPENDS:${PN} += "kernel-module-snd-bcm2835"
