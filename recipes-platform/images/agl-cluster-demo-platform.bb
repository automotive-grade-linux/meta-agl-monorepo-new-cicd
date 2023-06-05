DESCRIPTION = "AGL Cluster Demo Platform image currently contains a simple cluster interface."

LICENSE = "MIT"

require recipes-platform/images/agl-image-compositor.bb

IMAGE_FEATURES += "splash package-management ssh-server-openssh"

inherit features_check

REQUIRED_DISTRO_FEATURES = "wayland"

# add packages for cluster demo platform (include demo apps) here
IMAGE_INSTALL += " \
    packagegroup-agl-cluster-demo-platform \
    ${@bb.utils.contains("AGL_FEATURES", "agl-demo-preload", "cluster-demo-config", "", d)} \
    ${@bb.utils.contains("AGL_FEATURES", "agl-demo-preload", "weston-ini-conf-landscape-inverted", "weston-ini-conf-landscape", d)} \
    ${@bb.utils.contains("AGL_FEATURES", "AGLCI", "qemu-set-display", "", d)} \
    "
