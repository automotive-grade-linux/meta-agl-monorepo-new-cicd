require agl-demo-features.inc

FEATURE_PACKAGES_agl-demo-cluster-support = " \
    tbtnavi \
    kuksa-databroker-env-open \
"

IMAGE_FEATURES += " \
    kuksa-val-databroker \
    kuksa-val-databroker-client \
    ${@bb.utils.contains("DISTRO_FEATURES", "agl-devel", "can-test-tools" , "", d)} \
"
