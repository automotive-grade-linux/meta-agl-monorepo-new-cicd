SUMMARY = "An AGL small image just capable of allowing a device to boot."
LICENSE = "MIT"

inherit agl-core-image

IMAGE_INSTALL = "packagegroup-agl-image-minimal ${CORE_IMAGE_EXTRA_INSTALL}"

IMAGE_ROOTFS_SIZE ?= "8192"
IMAGE_ROOTFS_EXTRA_SPACE:append = "${@bb.utils.contains("DISTRO_FEATURES", "systemd", " + 4096", "", d)}"
