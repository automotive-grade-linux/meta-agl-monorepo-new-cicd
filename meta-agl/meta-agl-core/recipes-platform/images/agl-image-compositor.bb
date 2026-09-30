SUMMARY = "A very basic Wayland image with agl-compositor"
LICENSE = "MIT"

require agl-image-minimal.bb

inherit features_check

REQUIRED_DISTRO_FEATURES = "wayland"

SYSTEMD_DEFAULT_TARGET = "graphical.target"

IMAGE_INSTALL += " \
    packagegroup-agl-graphical-compositor \
    ${@bb.utils.contains('DISTRO_FEATURES', 'agl-devel', 'weston-examples', '', d)} \
"
