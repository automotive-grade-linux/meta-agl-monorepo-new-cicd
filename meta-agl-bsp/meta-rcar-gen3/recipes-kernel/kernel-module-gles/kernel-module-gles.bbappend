require checksum_control.inc

module_do_compile:prepend() {
    cd ${S}/build/linux/config/compilers
    cp aarch64-linux-gnu.mk ${TARGET_SYS}.mk
}

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

# Required to guarantee the module goes into the expected
# kernel-module-gles package and doesn't end up packaged in
# kernel-module-pvrsvkm by the default behavior.  Can be removed
# if upstream correctly use ${nonarch_base_libdir} themselves.
FILES:${PN} += "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/pvrsrvkm.ko"
