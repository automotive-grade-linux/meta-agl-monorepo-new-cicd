MACHINEOVERRIDES_append = ":agl-refhw-h3"

# Reference hardware has USB3
MACHINE_FEATURES_append = " usb3"

KERNEL_DEVICETREE_append = " renesas/r8a7795-agl-refhw.dtb"

# Disable the meta-agl-refhw-gen3 arm-trusted-firmware bbappend as
# it makes the firmware reference hardware specific.
BBMASK += "meta-agl-refhw-gen3/recipes-bsp/arm-trusted-firmware"
