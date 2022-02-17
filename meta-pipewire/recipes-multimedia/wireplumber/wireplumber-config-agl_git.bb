SUMMARY     = "AGL configuration file for wireplumber"
HOMEPAGE    = "https://gitlab.freedesktop.org/gkiagia/wireplumber"
BUGTRACKER  = "https://jira.automotivelinux.org"
AUTHOR      = "George Kiagiadakis <george.kiagiadakis@collabora.com>"
SECTION     = "multimedia"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
SRC_URI = "\
    file://bluetooth.lua.d/ \
    file://host.lua.d/ \
    file://00-functions.lua \
    file://alsa-suspend.lua \
    file://bluetooth.conf \
    file://wireplumber.conf \
    file://wireplumber-bluetooth.conf \
"
PACKAGE_ARCH = "${MACHINE_ARCH}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install:append() {
    config_dir="${D}${sysconfdir}/wireplumber/"
    scripts_dir="${D}${datadir}/wireplumber/scripts/"
    dbus_config_dir="${D}${sysconfdir}/dbus-1/system.d/"
    systemd_dir="${D}${sysconfdir}/systemd/system/pipewire.service.wants/"

    install -d ${config_dir}
    install -m 0644 ${WORKDIR}/00-functions.lua ${config_dir}

    # config of the main (host) instance
    install -d ${config_dir}/host.lua.d/
    ln -s ../00-functions.lua ${config_dir}/host.lua.d/00-functions.lua
    install -m 0644 ${WORKDIR}/host.lua.d/*.lua ${config_dir}/host.lua.d/
    install -m 0644 ${WORKDIR}/wireplumber.conf ${config_dir}

    # config of the bluetooth instance
    install -d ${config_dir}/bluetooth.lua.d/
    ln -s ../00-functions.lua ${config_dir}/bluetooth.lua.d/00-functions.lua
    install -m 0644 ${WORKDIR}/bluetooth.lua.d/*.lua ${config_dir}/bluetooth.lua.d/
    install -m 0644 ${WORKDIR}/bluetooth.conf ${config_dir}

    # install the alsa-suspend script, loaded by the main instance
    install -d ${scripts_dir}
    install -m 0644 ${WORKDIR}/alsa-suspend.lua ${scripts_dir}

    # install dbus daemon configuration
    install -d ${dbus_config_dir}
    install -m 0644 ${WORKDIR}/wireplumber-bluetooth.conf ${dbus_config_dir}

    # enable additional systemd services
    install -d ${systemd_dir}
    ln -s ${systemd_system_unitdir}/wireplumber@.service ${systemd_dir}/wireplumber@bluetooth.service
}

FILES:${PN} += "\
    ${sysconfdir}/* \
    ${datadir}/wireplumber/* \
"
CONFFILES:${PN} += "\
    ${sysconfdir}/* \
"
