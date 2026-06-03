# Include file intended for kernel-yocto.bbclass based recipes.

include linux-agl-config.inc

# Use alldefconfig for the qemu targets, but let other kernel-yocto
# based BSPs use their own defaults (note that linux-renesas and
# linux-raspberrypi also default to alldefconfig).
KCONFIG_MODE:qemuall = "--alldefconfig"
