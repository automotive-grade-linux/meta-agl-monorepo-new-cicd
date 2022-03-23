PACKAGECONFIG = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluez5', 'bluez', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'alsa', 'alsa pipewire-alsa', '', d)} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'agl-devel', 'sndfile', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'systemd', d)} \
    gstreamer v4l2 \
"

SRC_URI += "\
    file://0001-Revert-treewide-meson.build-use-project_-build-sourc.patch \
    file://0002-Revert-treewide-meson.build-use-feature.allowed.patch \
    file://0003-Revert-treewide-meson.build-use-dependency-variable-.patch \
    file://0004-Revert-treewide-meson.build-simplify-get_variable-ca.patch \
    file://0005-Revert-treewide-meson.build-get-SPA_PLUGIN_DIR-from-.patch \
    file://0006-Revert-meson-declare-spa_dep-and-override_dependency.patch \
    file://0007-Revert-meson-use-meson-variables-for-the-SMs-uninsta.patch \
    file://0008-Revert-meson-declare-spa_dep-and-override_dependency.patch \
    file://0009-Revert-test-add-test-for-the-loop.patch \
    file://0010-Revert-spa-improve-the-AEC-interface.patch \
    file://0011-Revert-module-echo-cancel-Move-backends-to-dynamic-l.patch \
    file://0012-Miscellanous-changes-to-account-for-lower-version-of.patch \
    file://0013-Revert-loop-remove-destroy-list.patch \
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
