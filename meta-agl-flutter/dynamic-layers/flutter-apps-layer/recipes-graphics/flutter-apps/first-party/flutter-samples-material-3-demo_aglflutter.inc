FILESEXTRAPATHS:prepend := "${THISDIR}/flutter-samples-material-3-demo:"

SRC_URI += "file://flutter-samples-material-3-demo.service \
            file://flutter-samples-material-3-demo.toml \
"

APP_CONFIG = "flutter-samples-material-3-demo.toml"

inherit systemd

# Required after meta-flutter 3.35.7 update, revisit in future
do_compile[network] = "1"

do_install:append() {
    install -D -m 0644 ${UNPACKDIR}/${BPN}.service ${D}${systemd_system_unitdir}/${BPN}.service
}

RDEPENDS:${PN} += "agl-flutter-env"

SYSTEMD_SERVICE:${PN} = "flutter-samples-material-3-demo.service"
