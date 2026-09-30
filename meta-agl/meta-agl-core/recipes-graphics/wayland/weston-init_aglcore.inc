SYSTEMD_AUTO_ENABLE = "enable"

do_install:append() {
    # Remove upstream weston.ini to avoid conflict with weston-ini-conf package
    rm -f ${D}${sysconfdir}/xdg/weston/weston.ini
}

RDEPENDS:${PN} += "weston-ini"
