SUMMARY = "DRM Lease LXC test guest image"
LICENSE = "MIT"

require guest-image-minimal.bb

IMAGE_INSTALL += " \
    packagegroup-agl-shared-demo-board-support \
    weston \
    weston-init-guest \
    weston-ini-conf-drm-lease-test-cluster \
"
