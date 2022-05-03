require checksum_control.inc

RDEPENDS:${PN}:append = " wayland-wsegl"

do_install:append(){
    sed -i 's/GROUP="video"/GROUP="display"/g' ${D}${sysconfdir}/udev/rules.d/72-pvr-seat.rules

    # Work around upstream not using ${nonarch_base_libdir}/firmware
    if ${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', 'true', 'false', d)}; then
        install -d ${D}${nonarch_base_libdir}/
        mv ${D}/lib/firmware ${D}${nonarch_base_libdir}/
        rm -rf ${D}/lib
    fi
}
