require recipes-platform/images/agl-ivi-demo-flutter-guest.bb

SUMMARY = "AGL KVM demo tradeshow guest IVI Flutter image"

# KUKSA.val always runs externally
IMAGE_FEATURES:remove = "kuksa-val-databroker"
KUKSA_CONF = "kuksa-conf-kvm-demo-tradeshow"

# Remove tbtnavi since it currently does not work wel
IMAGE_FEATURES:remove = "agl-demo-cluster-support"

# Everything runs on the host for now
PLATFORM_SERVICES_INSTALL = ""

# We do not want weston-terminal visible
IMAGE_INSTALL:remove = "weston-terminal-conf"

# We do not need pipewire streaming to the cluster w/o tbtnavi
IMAGE_INSTALL:remove = "agl-compositor-init-pipewire"

# Add KVM specific conf to have the homescreen start depend on
# network-online for the databroker to be visible.
IMAGE_INSTALL:append = " flutter-ics-homescreen-conf-kvm-tradeshow"
