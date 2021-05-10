PACKAGECONFIG = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa pipewire-alsa', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'agl-devel', 'sndfile', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
    gstreamer v4l2 \
"

SRC_URI += "\
    file://0001-pipewiresink-use-all-the-available-dest-memory-when-.patch \
    file://0002-pipewiresink-release-manually-acquired-buffers-back-.patch \
"
