FILESEXTRAPATHS:prepend := "${THISDIR}/flutter-samples-material-3-demo:"

SRC_URI += "file://flutter-samples-material-3-demo.service \
            file://flutter-samples-material-3-demo.toml \
"

APP_CONFIG = "flutter-samples-material-3-demo.toml"

inherit systemd

do_install:append() {
    install -D -m 0644 ${WORKDIR}/${BPN}.service ${D}${systemd_system_unitdir}/${BPN}.service
}

RDEPENDS:${PN} += "agl-flutter-env"

SYSTEMD_SERVICE:${PN} = "flutter-samples-material-3-demo.service"
