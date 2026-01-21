require recipes-platform/images/agl-ivi-demo-qt.bb

SUMMARY = "AGL IVI tradeshow demo Qt image"

IMAGE_FEATURES += "agl-demo-cluster-support"

# We do not want weston-terminal visible
IMAGE_INSTALL:remove = "weston-terminal-conf"

KUKSA_CONF = "kuksa-conf-demo-tradeshow"

IMAGE_INSTALL += " \
    weston-ini-conf-remoting \
    demo-i2c-udev-conf \
    simple-can-simulator \
"