SUMMARY = "A basic system of AGL distribution of IVI profile"

DESCRIPTION = "Basic image for baseline of AGL Distribution for IVI profile."

LICENSE = "MIT"

require recipes-platform/images/agl-image-compositor.bb

IMAGE_INSTALL += " \
    packagegroup-agl-image-ivi \
    packagegroup-agl-ivi-services \
    can-utils \
    iproute2 \
    "

IMAGE_FEATURES += "splash package-management ssh-server-openssh"


