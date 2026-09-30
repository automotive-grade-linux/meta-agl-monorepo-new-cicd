# Common cross-machine QEMU configuration

# Override default of "ext4.zst" to avoid needing to always use
# snapshot mode with runqemu.
QB_DEFAULT_FSTYPE ?= "ext4"

# Use our own wks file
WKS_FILE = "directdisk.wks.in"
