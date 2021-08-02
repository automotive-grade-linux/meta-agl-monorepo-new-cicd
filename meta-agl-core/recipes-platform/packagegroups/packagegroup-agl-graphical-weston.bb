DESCRIPTION = "The minimal set of packages required for Wayland support"
LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-agl-graphical-weston \
    "

ALLOW_EMPTY:${PN} = "1"

RDEPENDS:${PN} += " \
                  weston \
                  weston-init \
                  weston-examples \
                  "

