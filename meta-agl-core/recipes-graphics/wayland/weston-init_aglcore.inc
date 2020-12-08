do_install_append() {
    # Remove upstream weston.ini to avoid conflict with weston-ini-conf package
    rm -f ${D}${sysconfdir}/xdg/weston/weston.ini
}

SYSTEMD_AUTO_ENABLE = "enable"
