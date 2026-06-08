PACKAGECONFIG:class-target = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'zeroconf', 'avahi', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez bluez-opus ${BLUETOOTH_AAC} bluez-aptx', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd systemd-system-service', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'alsa vulkan', d)} \
    ${PIPEWIRE_SESSION_MANAGER} \
    ${FFMPEG_AVAILABLE} flatpak gstreamer gsettings libusb raop v4l2 udev volume libcamera \
    ${@bb.utils.contains('DISTRO_FEATURES', 'agl-devel', 'sndfile pw-cat readline', '', d)} \
"
