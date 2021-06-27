FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

require recipes-kernel/linux/linux-yocto-agl.inc

# Add ADSP patch to enable and add sound hardware abstraction
SRC_URI_append = " \
    file://0004-ADSP-enable-and-add-sound-hardware-abstraction.patch \
"

AGL_KCONFIG_FRAGMENTS += "namespace_fix.cfg"

# For Xen
AGL_KCONFIG_FRAGMENTS += " \
    ${@bb.utils.contains('AGL_XEN_WANTED','1','xen-be.cfg','',d)} \
"
SRC_URI_append_m3ulcb = " \
    ${@bb.utils.contains('AGL_XEN_WANTED','1','file://r8a7796-m3ulcb-xen.dts;subdir=git/arch/${ARCH}/boot/dts/renesas','',d)} \
"
KERNEL_DEVICETREE_append_m3ulcb = " \
    ${@bb.utils.contains('AGL_XEN_WANTED','1','renesas/r8a7796-m3ulcb-xen.dtb','',d)} \
"
