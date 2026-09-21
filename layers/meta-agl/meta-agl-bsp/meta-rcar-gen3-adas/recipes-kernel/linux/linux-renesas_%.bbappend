FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:ulcb = " \
    file://0001-arm64-dts-renesas-preserve-drm-HDMI-connector-naming.patch \
"

KERNEL_DEVICETREE:remove:h3ulcb = " \
    renesas/r8a7795-es1-h3ulcb-view.dtb \
    renesas/r8a7795-es1-h3ulcb-had-alfa.dtb \
    renesas/r8a7795-es1-h3ulcb-had-beta.dtb \
    renesas/r8a7795-es1-h3ulcb-vb.dtb \
    renesas/r8a7795-es1-h3ulcb-vb2.dtb \
    renesas/r8a7795-es1-h3ulcb-vbm.dtb \
    renesas/r8a7795-h3ulcb-view.dtb \
    renesas/r8a7795-h3ulcb-had-alfa.dtb \
    renesas/r8a7795-h3ulcb-had-beta.dtb \
    renesas/r8a7795-h3ulcb-vb.dtb \
    renesas/r8a7795-h3ulcb-vb2.dtb \
    renesas/r8a7795-h3ulcb-vb2.1.dtb \
    renesas/r8a7795-h3ulcb-vbm.dtb \
    renesas/r8a7795-h3ulcb-4x2g-vb.dtb \
    renesas/r8a7795-h3ulcb-4x2g-vb2.dtb \
    renesas/r8a7795-h3ulcb-4x2g-vb2.1.dtb \
    renesas/r8a7795-h3ulcb-4x2g-vbm.dtb \
"

# The meta-rcar layer actives by default the configuration MTD_RENESAS_RPC_HYPERFLASH
# in the kernel. We need to set DISABLE_RPC_ACCESS to deactivate it.
DISABLE_RPC_ACCESS ?= "1"
