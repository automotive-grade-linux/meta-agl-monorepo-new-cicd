require recipes-platform/images/agl-gateway-demo.bb

SUMMARY = "AGL gateway tradeshow demo image"

# First CAN interface will be connected to the demo control
# panel.
# Second CAN interface will be connected to the demo setup
# steering wheel & HVAC in the full demo.
IMAGE_INSTALL += " \
    kuksa-can-provider-conf-gw-hardware \
    vss-agl-gw-hardware \
    kuksa-can-provider-conf-gw-control-panel \
    vss-agl-gw-control-panel \
"
