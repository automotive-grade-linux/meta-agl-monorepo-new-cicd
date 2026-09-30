DESCRIPTION = "The minimal set of packages required for the Weston compositor"
LICENSE = "MIT"

inherit packagegroup features_check

# Reuse include file from upstream weston since we have the same requirements
require recipes-graphics/wayland/required-distro-features.inc

RDEPENDS:${PN} += " \
    weston \
    weston-init \
"
