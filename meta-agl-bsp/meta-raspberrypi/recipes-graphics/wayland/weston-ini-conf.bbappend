FILESEXTRAPATHS:prepend:raspberrypi := "${THISDIR}/${PN}:"

SRC_URI:append:raspberrypi = " file://dsi.cfg"
