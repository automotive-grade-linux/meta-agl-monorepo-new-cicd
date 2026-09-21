SUMMARY = "AGL Instrument Cluster minimized standalone demo image"
LICENSE = "MIT"

require recipes-platform/images/agl-image-boot.bb

IMAGE_FEATURES =+ "allow-empty-password empty-root-password allow-root-login post-install-logging"

#NO_RECOMMENDATIONS = "1"

AGLIC_CORE_PACKAGES = " \
    packagegroup-agl-ic-core \
    dlt-daemon \
    dlt-daemon-system \
"
AGLIC_DEMO_PACKAGES = " \
    packagegroup-agl-shared-demo-board-support-slint \
    agl-slint-cluster \
    liberation-fonts \
"
AGLIVI_DEMO_PACKAGES = " \
"

IMAGE_INSTALL += " \
    ${MACHINE_EXTRA_RRECOMMENDS} \
    kernel-image \
    ${AGLIC_DEMO_PACKAGES} \
"

IMAGE_INSTALL += " \
    ${@bb.utils.contains('AGL_FEATURES', 'agl-ic', '${AGLIC_CORE_PACKAGES}', '${AGLIVI_DEMO_PACKAGES}', d)} \
    ${@bb.utils.contains('AGL_FEATURES', 'agldemo', '${AGLIVI_DEMO_PACKAGES}', '', d)} \
"

# Enable SDK build support
require recipes-platform/images/agl-sdk-build-support.inc
