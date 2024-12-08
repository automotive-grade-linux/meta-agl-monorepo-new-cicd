# Add for gstreamer plugins ugly
LICENSE_FLAGS_ACCEPTED:append = " commercial"
MACHINE_FEATURES:append:rk3588 = " multimedia"

BBMASK += " \
    meta-rockchip/recipes-multimedia/gstreamer/ \
    meta-rockchip/recipes-graphics/wayland/weston_%.bbappend \
    "

MACHINE_EXTRA_RRECOMMENDS:append = " \
    alsa-state \
"
