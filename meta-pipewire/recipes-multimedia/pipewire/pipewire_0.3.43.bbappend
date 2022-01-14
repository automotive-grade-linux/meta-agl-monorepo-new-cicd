PACKAGECONFIG = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa pipewire-alsa', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'agl-devel', 'sndfile', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
    gstreamer v4l2 \
"

SRC_URI += "\
    file://0001-Revert-treewide-meson.build-use-project_-build-sourc.patch \
    file://0002-Revert-treewide-meson.build-use-dependency-variable-.patch \
    file://0003-Revert-meson-declare-spa_dep-and-override_dependency.patch \
    file://0004-Revert-systemd-correctly-prefix-systemd-system-units.patch \
"

do_install:append() {
    # install symlinks to alsalib configuration files
    for i in 50-pipewire.conf 99-pipewire-default.conf; do
        if [ -f ${D}${datadir}/alsa/alsa.conf.d/${i} ]; then
            mkdir -p ${D}${sysconfdir}/alsa/conf.d
            ln -s ${datadir}/alsa/alsa.conf.d/${i} ${D}${sysconfdir}/alsa/conf.d/${i}
        fi
    done
}

FILES:${PN}-alsa:append = "\
    ${sysconfdir}/alsa/conf.d/* \
"
