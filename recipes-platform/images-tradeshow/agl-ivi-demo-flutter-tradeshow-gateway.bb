require agl-ivi-demo-flutter-tradeshow.bb

SUMMARY = "AGL IVI tradeshow gateway demo Flutter image"

# We do not want a local databroker instance
IMAGE_FEATURES:remove = "kuksa-val-databroker"

KUKSA_CONF = "kuksa-conf-gateway-demo"
