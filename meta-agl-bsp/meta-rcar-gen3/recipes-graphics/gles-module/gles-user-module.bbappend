require checksum_control.inc

RDEPENDS:${PN}:append = " wayland-wsegl"

do_install:append(){
    # Work around upstream not using ${nonarch_base_libdir}/firmware
    if ${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', 'true', 'false', d)}; then
        install -d ${D}${nonarch_base_libdir}/
        mv ${D}/lib/firmware ${D}${nonarch_base_libdir}/
        rm -rf ${D}/lib
    fi

    # Undo upstream's out of date use of weston@.service
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)} ; then
        # Fix weston dependency, needs to be weston.service
        sed -i 's/^RequiredBy=weston@.service$/RequiredBy=weston.service/' \
                ${D}${systemd_system_unitdir}/rc.pvr.service
    fi
}
