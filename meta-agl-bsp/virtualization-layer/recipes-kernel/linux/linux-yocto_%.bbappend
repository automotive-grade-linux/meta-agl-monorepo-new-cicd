FILESEXTRAPATHS_prepend := "${THISDIR}/linux-yocto:"

SRC_URI_append_virtio-all = " \
    file://virtio-kmeta;type=kmeta;destsuffix=virtio-kmeta \
"

COMPATIBLE_MACHINE_virtio-aarch64 = "virtio-aarch64"
