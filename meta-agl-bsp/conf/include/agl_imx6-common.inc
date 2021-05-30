require agl_imx-common.inc

DISTRO_FEATURES_append = " agl-medium-arm-compiler"

# This disables the Freescale/NXP Vivante GPU driver and enables
# etnaviv support.  This is the default for the i.MX6 platforms in
# meta-freescale, but not for all the ones in meta-freescale-3rdparty,
# so set it here to give a consistent default.
IMX_DEFAULT_BSP = "mainline"

# Set CMA size large enough to work with AGL
UBOOT_EXTLINUX_KERNEL_ARGS_append = " cma=512M"

AGL_DEFAULT_INITRAMFS_FSTYPES := ""
