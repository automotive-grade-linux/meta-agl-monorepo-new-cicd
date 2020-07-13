require checksum_control.inc

do_install_append() {
    # Fix weston dependency, needs to be weston@.service
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)} ; then
        sed -i 's/^RequiredBy=weston.service$/RequiredBy=weston@.service/' ${D}${systemd_system_unitdir}/rc.pvr.service
    fi
}
