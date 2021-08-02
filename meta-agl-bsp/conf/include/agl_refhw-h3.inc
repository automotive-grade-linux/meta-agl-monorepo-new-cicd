AGL_FEATURES:append = " agl-refhw-h3"

# Reference hardware has USB3
MACHINE_FEATURES:append = " usb3"

KERNEL_DEVICETREE:append = " renesas/r8a7795-agl-refhw.dtb"

UBOOT_CONFIG:append = " agl-refhw-4x2g"
UBOOT_CONFIG[agl-refhw-4x2g] = "r8a7795_salvator-xs-4x2g_defconfig"
