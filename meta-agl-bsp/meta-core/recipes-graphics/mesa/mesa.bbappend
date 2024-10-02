FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

require ${@bb.utils.contains('AGL_FEATURES', 'aglcore', '${BPN}_agl.inc', '', d)}
