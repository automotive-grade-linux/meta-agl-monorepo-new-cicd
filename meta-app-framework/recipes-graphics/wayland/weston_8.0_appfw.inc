FILESEXTRAPATHS_append := ":${THISDIR}/weston"

SRC_URI_append = "\
    file://0001-Allow-regular-users-to-launch-Weston_7.0.0.patch \
    file://smack-weston \
    "

EXTRA_OEMESON_append = " -Denable-user-start=true"

do_install_append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'smack', 'true', 'false', d)}; then
        # Install SMACK rules
        install -D -m 0644 ${WORKDIR}/smack-weston ${D}${sysconfdir}/smack/accesses.d/weston
    fi
}

FILES_${PN}_append = "\
    ${sysconfdir}/smack/accesses.d/* \
"
