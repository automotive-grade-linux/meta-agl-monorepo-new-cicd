require agl-kvm-demo-flutter-tradeshow.bb

SUMMARY = "AGL KVM + gateway tradeshow Flutter demo image"

# We only want KUKSA.val client support, not the databroker (since
# that will be running on the gateway)
IMAGE_FEATURES:remove = "kuksa-val-databroker"
KUKSA_CONF = "kuksa-conf-gateway-demo"

# Not needed if we're not running the databroker
IMAGE_INSTALL:remove = " \
    kuksa-databroker-env-open \
"

IMAGE_INSTALL += "\
    ${@bb.utils.contains("DISTRO_FEATURES", "agl-devel", "kuksa-client" , "", d)} \
"

GUEST_VM1_IMAGE = "agl-ivi-demo-flutter-guest-tradeshow-gateway"
GUEST_VM2_IMAGE = "agl-cluster-demo-flutter-guest-tradeshow-gateway"
