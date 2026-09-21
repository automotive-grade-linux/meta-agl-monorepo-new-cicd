require recipes-platform/images/agl-ivi-demo-flutter.bb

SUMMARY = "AGL KVM demo guest IVI Flutter image"

# We assume there's always a cluster in the KVM demo
IMAGE_FEATURES += "agl-demo-cluster-support"

IMAGE_INSTALL += " \
    weston-pipewire \
    agl-compositor-init-pipewire \
    weston-ini-conf-remoting-kvm \
"
