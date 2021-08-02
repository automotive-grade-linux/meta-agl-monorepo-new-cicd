DESCRIPTION = "The minimal set of packages required by AGL"
LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-agl-image-minimal \
    profile-agl-minimal \
    "

ALLOW_EMPTY:${PN} = "1"

RDEPENDS:${PN} += "\
    packagegroup-agl-core-boot \
    packagegroup-machine-base \
    "

RDEPENDS:${PN} += "\
    packagegroup-agl-core-connectivity \
    packagegroup-agl-core-os-commonlibs \
    packagegroup-agl-core-security \
    "

RDEPENDS:profile-agl-minimal = "${PN}"
