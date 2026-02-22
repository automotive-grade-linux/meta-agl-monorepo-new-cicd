require recipes-platform/images/agl-cluster-demo-flutter-guest.bb

SUMMARY = "AGL KVM demo guest tradeshow cluster Flutter image"

KUKSA_CONF = "kuksa-conf-kvm-demo-tradeshow"

# The cluster screen is rotated in the full demo setup, so the
# default compositor configuration needs to be replaced.
IMAGE_INSTALL:remove = "weston-ini-conf-landscape"

# Remove receiver for now due to SPEC-5384
IMAGE_INSTALL:remove = "cluster-receiver"

IMAGE_INSTALL += " \
    psplash-inverted-config \
    weston-ini-conf-landscape-inverted \
"
