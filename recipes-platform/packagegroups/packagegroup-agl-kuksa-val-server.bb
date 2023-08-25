SUMMARY = "KUKSA.val server packages"
LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-agl-kuksa-val-server \
"

RDEPENDS:packagegroup-agl-kuksa-val-server = "\
    kuksa-val \
    kuksa-val-agl \
    kuksa-certificates-agl \
    kuksa-dbc-feeder \
"
