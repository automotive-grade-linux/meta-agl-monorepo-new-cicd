do_install:append() {
    # Add a rule to ensure the 'video' group has permission to access
    install -d ${D}${sysconfdir}/udev/rules.d
    cat >${D}${sysconfdir}/udev/rules.d/56-rgnmmbuf.rules <<'EOF'
KERNEL=="rgnmmbuf", MODE="0660", GROUP="video"
EOF
}

FILES:${PN}:append = " \
    ${sysconfdir}/udev/rules.d/*.rules \
"
