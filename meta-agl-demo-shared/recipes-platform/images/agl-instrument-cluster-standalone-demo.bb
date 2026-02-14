SUMMARY = "AGL Instrument Cluster minimized standalone demo image"
LICENSE = "MIT"

require recipes-platform/images/agl-image-boot.bb

NO_RECOMMENDATIONS = "1"

AGLIC_CORE_PACKAGES = " \
    packagegroup-agl-ic-core \
    dlt-daemon \
    dlt-daemon-system \
"
AGLIC_DEMO_PACKAGES = " \
    packagegroup-agl-ic-qt \
    packagegroup-agl-shared-demo-board-support \
    cluster-refgui \
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
