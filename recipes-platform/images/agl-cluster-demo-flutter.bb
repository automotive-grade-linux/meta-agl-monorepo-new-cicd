SUMMARY = "Baseline Flutter Image for Release"

LICENSE = "MIT"

require recipes-platform/images/agl-image-compositor.bb
require agl-demo-features.inc

IMAGE_FEATURES += "splash package-management ssh-server-openssh"

IMAGE_FEATURES += " \
    kuksa-val-databroker-client \
    kuksa-val-databroker \
"

AGL_DEVEL_INSTALL = " \
    simple-can-simulator \
"

# Generic
IMAGE_INSTALL += "\
    weston-ini-conf-landscape \
    \
    packagegroup-agl-networking \
    cluster-receiver \
    \
    ${@bb.utils.contains("DISTRO_FEATURES", "agl-devel", "${AGL_DEVEL_INSTALL}" , "", d)} \
"

# Application KUKSA configuration
KUKSA_CONF = "kuksa-conf"

IMAGE_INSTALL += "\
    flutter-auto \
    flutter-cluster-dashboard \
    ${KUKSA_CONF} \
    cluster-demo-config-flutter \
"

CLANGSDK = "1"

TOOLCHAIN_HOST_TASK:append = " nativesdk-flutter-sdk"
