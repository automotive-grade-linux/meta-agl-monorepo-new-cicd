SUMMARY     = "AGL configuration file for wireplumber"
HOMEPAGE    = "https://gitlab.freedesktop.org/gkiagia/wireplumber"
BUGTRACKER  = "https://jira.automotivelinux.org"
AUTHOR      = "George Kiagiadakis <george.kiagiadakis@collabora.com>"
SECTION     = "multimedia"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
SRC_URI = "\
    file://00-functions.lua \
    file://00-spa-libs.lua \
    file://10-default-policy.lua \
    file://30-alsa-monitor.lua \
    file://30-bluez-monitor.lua \
    file://99-load-modules.lua \
    file://wireplumber.conf \
    file://wireplumber-bluetooth.conf \
"
PACKAGE_ARCH = "${MACHINE_ARCH}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install_append() {
    config_dir="${D}${sysconfdir}/wireplumber/config.lua.d/"
    dbus_config_dir="${D}${sysconfdir}/dbus-1/system.d/"

    install -d ${config_dir}
    install -m 0644 ${WORKDIR}/00-functions.lua ${config_dir}
    install -m 0644 ${WORKDIR}/00-spa-libs.lua ${config_dir}
    install -m 0644 ${WORKDIR}/10-default-policy.lua ${config_dir}
    install -m 0644 ${WORKDIR}/30-alsa-monitor.lua ${config_dir}
    install -m 0644 ${WORKDIR}/30-bluez-monitor.lua ${config_dir}
    install -m 0644 ${WORKDIR}/99-load-modules.lua ${config_dir}

    install -m 0644 ${WORKDIR}/wireplumber.conf ${D}${sysconfdir}/wireplumber/

    install -d ${dbus_config_dir}
    install -m 0644 ${WORKDIR}/wireplumber-bluetooth.conf ${dbus_config_dir}
}

FILES_${PN} += "\
    ${sysconfdir}/wireplumber/* \
"
CONFFILES_${PN} += "\
    ${sysconfdir}/wireplumber/* \
"
RPROVIDES_${PN} += "virtual/wireplumber-config"
