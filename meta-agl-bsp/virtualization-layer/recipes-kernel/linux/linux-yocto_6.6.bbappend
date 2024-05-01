FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# Cannot just append to SRC_URI, as the metadata interferes with the
# AGL config fragment scheme
AGL_KERNEL_SRC:prepend:virtio-all = " \
    git://gerrit.automotivelinux.org/gerrit/src/agl-yocto-kernel-meta.git;protocol=https;type=kmeta;name=agl-meta;destsuffix=agl-kernel-meta;branch=kernel-5.15 \
    file://virtio-aarch64-${LINUX_KERNEL_TYPE}.scc \
"

SRCREV_agl-meta = "4deb7357eab5962b0553a5ad1f11be5ac35f9da9"

AGL_KCONFIG_FRAGMENTS:append:virtio-all = " \
    virtio-drm.cfg \
    virtio-pci.cfg \
"

COMPATIBLE_MACHINE:virtio-aarch64 = "virtio-aarch64"
