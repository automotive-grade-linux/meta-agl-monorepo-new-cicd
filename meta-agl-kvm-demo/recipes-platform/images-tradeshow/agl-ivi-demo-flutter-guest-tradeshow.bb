require recipes-platform/images/agl-ivi-demo-flutter-guest.bb

SUMMARY = "AGL KVM demo tradeshow guest IVI Flutter image"

# KUKSA.val always runs externally
IMAGE_FEATURES:remove = "kuksa-val-databroker"
KUKSA_CONF = "kuksa-conf-kvm-demo-tradeshow"

# Everything runs on the host for now
PLATFORM_SERVICES_INSTALL = ""

# We do not want weston-terminal visible
IMAGE_INSTALL:remove = "weston-terminal-conf"
