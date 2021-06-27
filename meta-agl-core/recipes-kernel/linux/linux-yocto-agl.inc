# Include file intended for kernel-yocto.bbclass based recipes.

include linux-agl-config.inc

# For qemuarm, build a generic v7 kernel instead of the arm926j one that
# upstream qemuarm defaults to.
KBUILD_DEFCONFIG_qemuarm = "multi_v7_defconfig"

# Use alldefconfig for the qemu targets, but let other kernel-yocto
# based BSPs use their own defaults (note that linux-renesas and
# linux-raspberrypi also default to alldefconfig).
KCONFIG_MODE_qemuall = "--alldefconfig"
