PACKAGECONFIG:class-target = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa pipewire-alsa', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'agl-devel', 'sndfile pw-cat readline', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd systemd-system-service', '', d)} \
    gstreamer v4l2 wireplumber \
"

do_install:append() {
    # install symlinks to alsalib configuration files
    for i in 50-pipewire.conf 99-pipewire-default.conf; do
        if [ -f ${D}${datadir}/alsa/alsa.conf.d/${i} ]; then
            install -d ${D}${sysconfdir}/alsa/conf.d
            ln -s ${datadir}/alsa/alsa.conf.d/${i} ${D}${sysconfdir}/alsa/conf.d/${i}
        fi
    done
}

FILES:${PN}-alsa:append = "\
    ${sysconfdir}/alsa/conf.d/* \
"
