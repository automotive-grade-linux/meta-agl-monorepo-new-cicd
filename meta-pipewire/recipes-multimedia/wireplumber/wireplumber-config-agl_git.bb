SUMMARY     = "AGL configuration file for wireplumber"
HOMEPAGE    = "https://gitlab.freedesktop.org/gkiagia/wireplumber"
BUGTRACKER  = "https://jira.automotivelinux.org"
AUTHOR      = "George Kiagiadakis <george.kiagiadakis@collabora.com>"
SECTION     = "multimedia"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://20-AGL-log.conf \
    file://20-AGL-profiles.conf \
    file://30-AGL-alsa.conf \
    file://30-AGL-bluetooth.conf \
    file://50-AGL-pw-ic-ipc.conf \
    file://alsa-suspend.lua \
    file://wireplumber-bluetooth.conf \
"

S = "${UNPACKDIR}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install:append() {
    config_dir="${D}${sysconfdir}/wireplumber/wireplumber.conf.d/"
    scripts_dir="${D}${datadir}/wireplumber/scripts/"
    dbus_config_dir="${D}${sysconfdir}/dbus-1/system.d/"
    systemd_dir="${D}${sysconfdir}/systemd/system/pipewire.service.wants/"

    # install the configuration fragments
    install -d ${config_dir}
    install -m 0644 ${UNPACKDIR}/20-AGL-log.conf ${config_dir}
    install -m 0644 ${UNPACKDIR}/20-AGL-profiles.conf ${config_dir}
    install -m 0644 ${UNPACKDIR}/30-AGL-alsa.conf ${config_dir}
    install -m 0644 ${UNPACKDIR}/30-AGL-bluetooth.conf ${config_dir}
    install -m 0644 ${UNPACKDIR}/50-AGL-pw-ic-ipc.conf ${config_dir}

    # install the alsa-suspend script, loaded by the audio instance
    install -d ${scripts_dir}
    install -m 0644 ${UNPACKDIR}/alsa-suspend.lua ${scripts_dir}

    # install dbus daemon configuration
    install -d ${dbus_config_dir}
    install -m 0644 ${UNPACKDIR}/wireplumber-bluetooth.conf ${dbus_config_dir}

    # enable additional systemd services
    install -d ${systemd_dir}
    ln -s ${systemd_system_unitdir}/wireplumber@.service ${systemd_dir}/wireplumber@audio.service
    ln -s ${systemd_system_unitdir}/wireplumber@.service ${systemd_dir}/wireplumber@video-capture.service
    ln -s ${systemd_system_unitdir}/wireplumber@.service ${systemd_dir}/wireplumber@bluetooth.service
}

FILES:${PN} += "\
    ${sysconfdir}/* \
    ${datadir}/wireplumber/* \
"
CONFFILES:${PN} += "\
    ${sysconfdir}/* \
"
