# Default IMAGE FSTYPES - tegraflash for flashing and ext3 for netboot
AGL_DEFAULT_IMAGE_FSTYPES := "tegraflash ext3.xz"
# Default initramfs fstypes - ext3 for netboot and cpio.gz for flashing
AGL_DEFAULT_INITRAMFS_FSTYPES := "ext3.gz cpio.gz"

# NOTE: we avoid ext4 for now due to optional features introduced in newer
# e2fsprogs and older kernels (e.g. for the tegra) not supporting these, yet.
# Downgrade filesystem to ext3
IMAGE_TEGRAFLASH_FS_TYPE = "ext3"

# DISABLE spdx ... fails on nvidia specific package
INHERIT:remove = "create-spdx"

# enforce nvme0n1p1
TNSPEC_BOOTDEV:forcevariable = "nvme0n1p1"
TNSPEC_BOOTDEV_DEFAULT:forcevariable = "nvme0n1p1"
