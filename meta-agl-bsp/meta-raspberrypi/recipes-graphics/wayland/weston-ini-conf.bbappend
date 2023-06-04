FILESEXTRAPATHS:prepend:raspberrypi := "${THISDIR}/${PN}:"

# OLD DSI config
#SRC_URI:append:raspberrypi = " file://dsi.cfg"

# two hdmi ports
SRC_URI:append:raspberrypi = " file://hdmi-dual.cfg"
