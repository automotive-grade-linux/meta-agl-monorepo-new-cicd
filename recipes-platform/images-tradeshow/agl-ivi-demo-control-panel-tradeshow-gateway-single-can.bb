require recipes-platform/images/agl-ivi-demo-control-panel.bb

SUMMARY = "AGL gateway demo control panel only image"

IMAGE_FEATURES += "kuksa-val-databroker"

IMAGE_INSTALL += " \
    agl-demo-control-panel-conf-gateway-demo \
    vss-agl-control-panel-all \
    kuksa-can-provider-conf-bidirectional \
"
