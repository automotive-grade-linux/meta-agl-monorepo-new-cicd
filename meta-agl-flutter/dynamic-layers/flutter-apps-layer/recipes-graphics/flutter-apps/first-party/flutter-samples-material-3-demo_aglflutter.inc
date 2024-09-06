FILESEXTRAPATHS:prepend := "${THISDIR}/flutter-samples-material-3-demo:"

SRC_URI += "file://flutter-samples-material-3-demo.service"

APP_CONFIG = "flutter-samples-material-3-demo.toml"

inherit systemd

do_install:append() {
    install -D -m 0644 ${WORKDIR}/${BPN}.service ${D}${systemd_system_unitdir}/${BPN}.service

    # determine build type based on what flutter-engine installed.
    for runtime_mode in ${FLUTTER_RUNTIME_MODES}
    do
        install -D -m 0644 ${WORKDIR}/${APP_CONFIG} \
            ${D}${datadir}/flutter/${PUBSPEC_APPNAME}/${FLUTTER_SDK_VERSION}/${runtime_mode}/config.toml
    done
}

RDEPENDS:${PN} += "agl-flutter-env"

SYSTEMD_SERVICE:${PN} = "flutter-samples-material-3-demo.service"
