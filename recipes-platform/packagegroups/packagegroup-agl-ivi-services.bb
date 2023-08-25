DESCRIPTION = "The minimal set of services to support AGL IVI demo"
LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-agl-ivi-services \
    "

RDEPENDS:${PN} += "\
    agl-service-hvac \
    agl-service-audiomixer \
    agl-service-radio \
    "
