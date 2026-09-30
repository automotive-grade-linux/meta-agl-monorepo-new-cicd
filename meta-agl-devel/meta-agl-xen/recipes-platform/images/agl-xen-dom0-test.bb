require agl-xen-dom0-minimal.bb

DESCRIPTION = "AGL test Xen dom0 image"

IMAGE_INSTALL += " \
    agl-xen-domu-test-config \
"
