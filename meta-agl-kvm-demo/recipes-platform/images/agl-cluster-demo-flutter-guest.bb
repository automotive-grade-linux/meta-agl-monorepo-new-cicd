require recipes-platform/images/agl-cluster-demo-flutter.bb

SUMMARY = "AGL KVM demo guest cluster Flutter image"

# We do not want a local databroker instance
IMAGE_FEATURES:remove = "kuksa-val-databroker"
KUKSA_CONF = "kuksa-conf-kvm-demo"
