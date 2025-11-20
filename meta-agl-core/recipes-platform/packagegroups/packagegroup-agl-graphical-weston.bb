DESCRIPTION = "The minimal set of packages required for the Weston compositor"
LICENSE = "MIT"

inherit packagegroup features_check

# weston needs wayland in DISTRO_FEATURES
REQUIRED_DISTRO_FEATURES = "wayland"

RDEPENDS:${PN} += " \
    weston \
    weston-init \
"
