DESCRIPTION = "The minimal set of packages required for the AGL compositor"
LICENSE = "MIT"

inherit packagegroup

# Reuse include file from upstream weston since we have the same requirements
require recipes-graphics/wayland/required-distro-features.inc

RDEPENDS:${PN} += " \
    agl-compositor \
    agl-compositor-init \
"
