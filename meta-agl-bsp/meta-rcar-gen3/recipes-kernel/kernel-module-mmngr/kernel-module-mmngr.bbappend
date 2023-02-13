KERNEL_MODULE_AUTOLOAD:append = " mmngr"
KERNEL_MODULE_PACKAGE_SUFFIX = ""

do_install:append() {
    # Work around upstream not using ${nonarch_base_libdir}/modules
    if ${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', 'true', 'false', d)}; then
        if [ -d ${D}/lib/modules ]; then
            install -d ${D}${nonarch_base_libdir}/
            mv ${D}/lib/modules ${D}${nonarch_base_libdir}/
            rm -rf ${D}/lib
        fi
    fi
}
