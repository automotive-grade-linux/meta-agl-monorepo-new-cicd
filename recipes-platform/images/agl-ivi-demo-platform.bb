require agl-image-ivi.bb

DESCRIPTION = "AGL Demo Platform image currently contains a simple HMI and demos."

require agl-demo-features.inc
require agl-demo-container-guest-integration.inc

# Add packages for demo platform (include demo apps) here

AGL_DEVEL_INSTALL += "\
    packagegroup-agl-kuksa-val-databroker-devel \
    simple-can-simulator \
"

IMAGE_INSTALL += " \
    packagegroup-agl-demo-platform \
    ${@bb.utils.contains("AGL_FEATURES", "agl-demo-preload", "", "weston-terminal-conf", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "agl-devel", "${AGL_DEVEL_INSTALL}" , "", d)} \
"

