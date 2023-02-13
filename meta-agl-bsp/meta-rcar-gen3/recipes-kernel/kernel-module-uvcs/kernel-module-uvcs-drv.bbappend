require checksum_control.inc

KERNEL_MODULE_AUTOLOAD = "uvcs_drv"

do_install:append() {
    # Work around upstream not using ${nonarch_base_libdir}/modules
    if ${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', 'true', 'false', d)}; then
        if [ -d ${D}/lib/modules ]; then
            install -d ${D}${nonarch_base_libdir}/
            mv ${D}/lib/modules ${D}${nonarch_base_libdir}/
            rm -rf ${D}/lib
        fi
    fi

    # Add a rule to ensure the 'display' user has permission to access
    install -d ${D}${sysconfdir}/udev/rules.d
    cat >${D}${sysconfdir}/udev/rules.d/56-uvcs.rules <<'EOF'
KERNEL=="uvcs", MODE="0660", GROUP="display"
EOF
}

FILES:${PN}:append = " \
    ${sysconfdir}/udev/rules.d/*.rules \
"
