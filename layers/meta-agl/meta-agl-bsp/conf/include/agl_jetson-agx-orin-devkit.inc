# Force using the newer linux-yocto kernel by default.
# This is in part required to fix building perf, but seems like a better
# idea in the longer term vs sticking on the older 5.15.x kernel.
PREFERRED_PROVIDER_virtual/kernel:jetson-agx-orin-devkit ?= "linux-yocto"

# Default IMAGE FSTYPES - tegraflash for flashing and ext3 for netboot
AGL_DEFAULT_IMAGE_FSTYPES := "tegraflash-tar.zst ext3.xz"
# Default initramfs fstypes - ext3 for netboot and cpio.gz for flashing
AGL_DEFAULT_INITRAMFS_FSTYPES := "ext3.gz cpio.gz"

# enforce nvme0n1p1
TNSPEC_BOOTDEV:forcevariable = "nvme0n1p1"
TNSPEC_BOOTDEV_DEFAULT:forcevariable = "nvme0n1p1"
