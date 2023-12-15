# DEFAULTTUNE overrides for AGL on ARM 32bit

# We should not need the tuning include below as the BSP should include the right set already.
# A double inclusion would produce a warning. This include line is just for reference
#   include conf/machine/include/arm/arch-armv7ve.inc
# medium profile boards need to
#   include conf/machine/include/arch/arm-armv7-a.inc

# Standard target for 32bit ARM (newer than cortex-a15)
ARM_DEFAULTTUNE = "armv7vethf-neon-vfpv4"

# for cortex-a8, cortex-a9, cortex-a7 (=rpi 2/3)
ARM_DEFAULTTUNE := "${@bb.utils.contains('DISTRO_FEATURES', 'agl-medium-arm-compiler', 'armv7athf-neon', '${ARM_DEFAULTTUNE}', d)}"

# for armv6 (=rpi0/1)
ARM_DEFAULTTUNE := "${@bb.utils.contains('DISTRO_FEATURES', 'agl-low-arm-compiler', 'arm1176jzfshf', '${ARM_DEFAULTTUNE}', d)}"

AGL_DEFAULTTUNE ?= "${ARM_DEFAULTTUNE}"
DEFAULTTUNE:agldefaulttune := "${AGL_DEFAULTTUNE}"

QB_SYSTEM_NAME ?= "qemu-system-arm"
QB_DEFAULT_KERNEL ?= "zImage"
QB_DEFAULT_FSTYPE ?= "ext4"
QB_MACHINE ?= "${@bb.utils.contains('DISTRO_FEATURES', 'agl-medium-arm-compiler', '-machine vexpress-a9', '-machine vexpress-a15', d)}"
QB_CPU ?= "${@bb.utils.contains('DISTRO_FEATURES', 'agl-medium-arm-compiler', '-cpu cortex-a9', '-cpu cortex-a15', d)}"
