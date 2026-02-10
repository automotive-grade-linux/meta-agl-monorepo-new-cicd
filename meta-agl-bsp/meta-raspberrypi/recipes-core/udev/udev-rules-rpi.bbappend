FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:raspberrypi5 = " \
    file://99-wlan-up.rules \
"

do_install:append:raspberrypi5() {
    install -m 0644 ${WORKDIR}/99-wlan-up.rules ${D}${sysconfdir}/udev/rules.d/
}

FILES:${PN}:append:raspberrypi5 = " \
    ${sysconfdir}/udev/rules.d/99-wlan-up.rules \
"
