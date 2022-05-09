FILESEXTRAPATHS:prepend := "${THISDIR}/linux-yocto:"

SRC_URI:append:virtio-all = " \
    file://virtio-kmeta;type=kmeta;destsuffix=virtio-kmeta \
    file://virtio-kmeta/bsp/virtio/virtio-aarch64-${LINUX_KERNEL_TYPE}.scc \
"

COMPATIBLE_MACHINE:virtio-aarch64 = "virtio-aarch64"
