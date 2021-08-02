FILESEXTRAPATHS:append := ":${THISDIR}/weston"

SRC_URI:append = "\
    file://0001-Allow-regular-users-to-launch-Weston_7.0.0.patch \
    file://smack-weston \
    "

EXTRA_OEMESON:append = " -Denable-user-start=true"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'smack', 'true', 'false', d)}; then
        # Install SMACK rules
        install -D -m 0644 ${WORKDIR}/smack-weston ${D}${sysconfdir}/smack/accesses.d/weston
    fi
}

FILES:${PN}:append = "\
    ${sysconfdir}/smack/accesses.d/* \
"
