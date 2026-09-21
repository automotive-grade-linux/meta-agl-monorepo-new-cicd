SUMMARY = "A very basic Wayland image with a terminal"
LICENSE = "MIT"

require agl-image-weston.bb

IMAGE_FEATURES += "splash"

IMAGE_INSTALL += " \
    busybox \
    gdb \
    gperf \
    weston-examples \
    curl \
    ptest-runner \
    "
