FILESEXTRAPATHS_prepend_raspberrypi := "${THISDIR}/${PN}:"

SRC_URI_append_raspberrypi = " file://dsi.cfg"
