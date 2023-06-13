DESCRIPTION = "The minimal set of packages required for the Weston compositor"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} += " \
    weston \
    weston-init \
"
