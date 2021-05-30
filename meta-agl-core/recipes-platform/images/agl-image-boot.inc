IMAGE_LINGUAS = " "

inherit core-image

IMAGE_INSTALL = "packagegroup-agl-core-boot ${CORE_IMAGE_EXTRA_INSTALL}"

IMAGE_ROOTFS_SIZE ?= "8192"
