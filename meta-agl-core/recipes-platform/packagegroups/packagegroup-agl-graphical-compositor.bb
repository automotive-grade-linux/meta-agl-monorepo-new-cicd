DESCRIPTION = "The minimal set of packages required for the AGL compositor"
LICENSE = "MIT"

inherit packagegroup

RDEPENDS:${PN} += " \
    agl-compositor \
    agl-compositor-init \
"
