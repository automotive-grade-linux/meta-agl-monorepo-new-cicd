PACKAGECONFIG:class-target = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa pipewire-alsa', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'agl-devel', 'sndfile pw-cat readline', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd systemd-system-service', '', d)} \
    gstreamer \
    v4l2 \
    wireplumber \
"
