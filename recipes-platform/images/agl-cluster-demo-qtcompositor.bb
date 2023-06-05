DESCRIPTION = "AGL Cluster Demo Qtwayland Compositor image currently contains a \
simple cluster interface."

LICENSE = "MIT"

require recipes-platform/images/agl-image-weston.bb

IMAGE_FEATURES += "splash package-management ssh-server-dropbear"

# Add packages for qtcompositor demo
IMAGE_INSTALL += "cluster-gauges-qtcompositor"
