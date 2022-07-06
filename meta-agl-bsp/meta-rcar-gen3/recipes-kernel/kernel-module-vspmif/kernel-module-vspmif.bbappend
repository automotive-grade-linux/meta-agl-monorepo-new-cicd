KERNEL_MODULE_AUTOLOAD:append = " vspm_if"
KERNEL_MODULE_PACKAGE_SUFFIX = ""

do_install:append() {
    # Work around upstream not using ${nonarch_base_libdir}/modules
    if ${@bb.utils.contains('DISTRO_FEATURES', 'usrmerge', 'true', 'false', d)}; then
        install -d ${D}${nonarch_base_libdir}/
        mv ${D}/lib/modules ${D}${nonarch_base_libdir}/
        rm -rf ${D}/lib
    fi

    # Add a rule to ensure the 'display' user has permission to access
    install -d ${D}${sysconfdir}/udev/rules.d
    cat >${D}${sysconfdir}/udev/rules.d/56-vspm_if.rules <<'EOF'
KERNEL=="vspm_if", MODE="0660", GROUP="display"
EOF
}

# Required to guarantee the module goes into the expected
# kernel-module-vspmif package and doesn't end up packaged in
# kernel-module-vspm-if by the default behavior.  Can be removed if
# upstream correctly use ${nonarch_base_libdir} themselves.
FILES:${PN} += "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra/vspm_if.ko"

#kernel-module-vspmif should not provide "kernel-module-vspm-if". "kernel-module-vspm-if" is a separate package with module rules.
RPROVIDES:${PN}:remove = "kernel-module-vspm-if"

FILES:${PN}:append = " \
    ${sysconfdir}/udev/rules.d/*.rules \
"
