FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

require recipes-kernel/linux/linux-yocto-agl.inc

SRC_URI:append = " \
    ${@oe.utils.conditional('USE_FAYTECH_MONITOR', '1', 'file://0002-faytech-fix-rpi.patch', '', d)} \
    file://0001-mconf-menuconfig.patch \
"

# Enable support for Pi foundation touchscreen
AGL_KCONFIG_FRAGMENTS += "raspberrypi-panel.cfg"

# Enable bt hci uart
AGL_KCONFIG_FRAGMENTS += "raspberrypi-hciuart.cfg"

# ENABLE NETWORK (built-in)
AGL_KCONFIG_FRAGMENTS += "raspberrypi_network.cfg"

# For Xen
AGL_KCONFIG_FRAGMENTS += " \
    ${@bb.utils.contains('AGL_XEN_WANTED','1','xen-be.cfg','',d)} \
"

# Take in account that linux under Xen should use the hvc0 console
SERIAL_OPTION = "${@bb.utils.contains('AGL_XEN_WANTED','1','hvc0','ttyS0,115200',d)}"
SERIAL = "${@oe.utils.conditional("ENABLE_UART", "1", "console=${SERIAL_OPTION}", "", d)}"

CMDLINE_DEBUG = ""

# Xen related option
CMDLINE:append = ' ${@bb.utils.contains('AGL_XEN_WANTED','1','clk_ignore_unused','',d)}'

# Workaround for crash during brcmfmac loading. Disable it at this moment
CMDLINE:append = ' ${@bb.utils.contains('AGL_XEN_WANTED','1','modprobe.blacklist=brcmfmac','',d)}'

CMDLINE:append = " usbhid.mousepoll=0"

# Add options to allow CMA to operate
CMDLINE:append = '${@oe.utils.conditional("ENABLE_CMA", "1", " coherent_pool=6M smsc95xx.turbo_mode=N", "", d)}'

KERNEL_MODULE_AUTOLOAD += "snd-bcm2835"
KERNEL_MODULE_AUTOLOAD += "hid-multitouch"

PACKAGES += "kernel-module-snd-bcm2835"

RDEPENDS:${PN} += "kernel-module-snd-bcm2835"
