FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-virgl-don-t-a-use-staging-when-a-resources-created-w.patch"

require ${@bb.utils.contains('AGL_FEATURES', 'aglcore', '${BPN}_agl.inc', '', d)}
