require recipes-platform/images/agl-gateway-demo.bb

SUMMARY = "AGL gateway tradeshow demo image - single CAN interface"

# Assumes only single CAN connection on can0
IMAGE_INSTALL += " \
    kuksa-can-provider-conf-bidirectional \
    vss-agl-gw-single-can \
"
