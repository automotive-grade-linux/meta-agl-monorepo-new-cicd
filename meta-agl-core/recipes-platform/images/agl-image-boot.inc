IMAGE_LINGUAS = " "

AGL_BASE_IMAGE ?= "core-image"

inherit ${AGL_BASE_IMAGE}

FEATURE_PACKAGES_selinux = " \
    packagegroup-agl-core-selinux \
    ${@bb.utils.contains('DISTRO_FEATURES', 'agl-devel', 'packagegroup-agl-core-selinux-devel', '', d)} \
"

IMAGE_FEATURES:append = " ${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"

IMAGE_INSTALL = "packagegroup-agl-core-boot ${CORE_IMAGE_EXTRA_INSTALL}"

IMAGE_ROOTFS_SIZE ?= "8192"
