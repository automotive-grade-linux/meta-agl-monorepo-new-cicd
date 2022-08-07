FILESEXTRAPATHS:prepend := "${THISDIR}/linux-yocto:"

SRC_URI:append:virtio-all = " \
    git://gerrit.automotivelinux.org/gerrit/src/agl-yocto-kernel-meta.git;protocol=https;type=kmeta;name=agl-meta;destsuffix=agl-kernel-meta;branch=master \
    file://virtio-aarch64-${LINUX_KERNEL_TYPE}.scc \
"

SRCREV_agl-meta = "c5008f4ba9e1b9f11c1014b53477079e605ceab7"

COMPATIBLE_MACHINE:virtio-aarch64 = "virtio-aarch64"
