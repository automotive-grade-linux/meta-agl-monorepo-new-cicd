# Add for gstreamer plugins ugly
LICENSE_FLAGS_ACCEPTED:append = " commercial"

BBMASK += " \
    meta-rockchip/recipes-multimedia/gstreamer/gstreamer1.0-plugins* \
    meta-rockchip/recipes-graphics/wayland/weston_%.bbappend \
    "

MACHINE_EXTRA_RRECOMMENDS:append = " \
    alsa-state \
"
