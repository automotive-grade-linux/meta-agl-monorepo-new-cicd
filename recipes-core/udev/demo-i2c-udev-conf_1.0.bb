SUMMARY = "USB attached I2C demo hardware udev configuration"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://hvac-conf-in-rewrite.sh \
           file://rtc-i2c-attach.sh \
           file://hvac-conf-in-rewrite@.service \
           file://rtc-i2c-attach@.service \
           file://99-agl-led-rtc.rules \
           file://agl-service-hvac-leds.toml.in \
"

S = "${UNPACKDIR}"

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sysconfdir}/xdg/AGL/agl-service-hvac
    install -m 0644 ${UNPACKDIR}/agl-service-hvac-leds.toml.in ${D}${sysconfdir}/xdg/AGL/agl-service-hvac/leds.toml.in

    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/hvac-conf-in-rewrite.sh ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/rtc-i2c-attach.sh ${D}${sbindir}

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/hvac-conf-in-rewrite@.service ${D}${systemd_system_unitdir}
        install -m 0644 ${UNPACKDIR}/rtc-i2c-attach@.service ${D}${systemd_system_unitdir}

        install -d ${D}${sysconfdir}/udev/rules.d
        install -m 0644 ${UNPACKDIR}/99-agl-led-rtc.rules ${D}${sysconfdir}/udev/rules.d/
    fi
}

FILES:${PN} += "${systemd_unitdir}"

RDEPENDS:${PN} += "bash"
