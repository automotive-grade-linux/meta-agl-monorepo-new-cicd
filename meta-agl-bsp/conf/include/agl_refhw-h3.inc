AGL_FEATURES_append = " agl-refhw-h3"

# Reference hardware has USB3
MACHINE_FEATURES_append = " usb3"

KERNEL_DEVICETREE_append = " renesas/r8a7795-agl-refhw.dtb"

UBOOT_CONFIG_append = " agl-refhw-4x2g"
UBOOT_CONFIG[agl-refhw-4x2g] = "r8a7795_salvator-xs-4x2g_defconfig"
