SYSTEMD_AUTO_ENABLE = "enable"

do_install_append() {
    # Remove upstream weston.ini to avoid conflict with weston-ini-conf package
    rm -f ${D}${sysconfdir}/xdg/weston/weston.ini
}

RDEPENDS_${PN} += "weston-ini"
