FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

require recipes-kernel/linux/linux-yocto-agl.inc

# Add ADSP patch to enable and add sound hardware abstraction
SRC_URI:append = " \
    file://0004-ADSP-enable-and-add-sound-hardware-abstraction.patch \
"

AGL_KCONFIG_FRAGMENTS += "namespace_fix.cfg"
AGL_KCONFIG_FRAGMENTS += "Set_GOV_PERFORMANCE.cfg"
AGL_KCONFIG_FRAGMENTS += "vivid.cfg"
