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
    packagegroup-agl-core-connectivity \
    "

RDEPENDS:profile-agl-minimal = "${PN}"
