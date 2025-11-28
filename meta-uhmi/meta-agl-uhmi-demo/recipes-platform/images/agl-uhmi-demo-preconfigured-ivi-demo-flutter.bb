SUMMARY = "AGL Unified HMI demo preconfigured agl-ivi-demo-flutter image"
LICENSE = "MIT"

require recipes-platform/images/agl-ivi-demo-flutter.bb

IMAGE_FEATURES += "ssh-server-openssh"

# Add packages for Unified HMI demo platform here
IMAGE_INSTALL += " \
    packagegroup-rvgpu \
    packagegroup-ddfw \
    uhmi-config-sender \
    uhmi-udev-conf \
    glmark2 \
"

UHMI_HOSTNAME ?= "agl-host0"
require recipes-config/uhmi-config/set-hostname.inc
