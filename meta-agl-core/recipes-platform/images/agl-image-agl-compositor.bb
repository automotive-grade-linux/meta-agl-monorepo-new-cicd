SUMMARY = "A very basic Wayland image with agl-compositor"

require agl-image-weston.inc

LICENSE = "MIT"

IMAGE_INSTALL:append = "\
    agl-compositor \
    packagegroup-agl-profile-graphical \
    "
