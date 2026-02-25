SUMMARY = "LXC cluster demo guest image"
LICENSE = "MIT"

require guest-image-minimal.bb

IMAGE_INSTALL += " \
    packagegroup-agl-ic-core \
    packagegroup-drm-lease-client-support \
    packagegroup-agl-shared-demo-board-support \
    packagegroup-agl-ic-qt \
    cluster-refgui \
"

IMAGE_OVERHEAD_FACTOR = "0"
EXTRA_IMAGECMD:append = " -L agl-cluster"
IMAGE_ROOTFS_EXTRA_SPACE = "0"
IMAGE_ROOTFS_SIZE = "1048576"

# Enable SDK build support
require recipes-platform/images/agl-sdk-build-support.inc
require recipes-platform/images/agl-sdk-build-support-qt6.inc
