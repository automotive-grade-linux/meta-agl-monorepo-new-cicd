FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:virtio-all = " \
    git://gerrit.automotivelinux.org/gerrit/src/agl-yocto-kernel-meta.git;protocol=https;type=kmeta;name=agl-meta;destsuffix=agl-kernel-meta;branch=kernel-5.15 \
    file://virtio-aarch64-${LINUX_KERNEL_TYPE}.scc \
"

SRCREV_agl-meta = "4deb7357eab5962b0553a5ad1f11be5ac35f9da9"

COMPATIBLE_MACHINE:virtio-aarch64 = "virtio-aarch64"
