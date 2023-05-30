DESCRIPTION = "The minimal set of services to support AGL IVI demo"
LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-agl-ivi-services \
    packagegroup-agl-ivi-services-devel \
    "

RDEPENDS:${PN} += "\
    kuksa-val \
    kuksa-databroker \
    kuksa-val-agl \
    kuksa-databroker-agl \
    kuksa-certificates-agl \
    kuksa-dbc-feeder \
    kuksa-vss-init \
    agl-service-hvac \
    agl-service-audiomixer \
    agl-service-radio \
    "

RDEPENDS:${PN}-devel += "\
    kuksa-databroker-cli \
    "
