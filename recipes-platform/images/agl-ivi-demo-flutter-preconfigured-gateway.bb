require agl-ivi-demo-flutter-preconfigured.bb

SUMMARY = "AGL IVI preconfigured gateway demo Flutter image"

# We do not want a local databroker instance
IMAGE_FEATURES:remove = "kuksa-val-databroker"

KUKSA_CONF = "kuksa-conf-gateway-demo"
