SUMMARY     = "AGL configuration file for wireplumber policy"
HOMEPAGE    = "https://gitlab.freedesktop.org/gkiagia/wireplumber"
BUGTRACKER  = "https://jira.automotivelinux.org"
AUTHOR      = "Ashok Sidipotu <ashok.sidipotu@collabora.com>"
SECTION     = "multimedia"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
SRC_URI = "\
    file://policy.lua.d \
    file://00-functions.lua \
    file://policy.conf \
"
PACKAGE_ARCH = "${MACHINE_ARCH}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install:append() {
    config_dir="${D}${sysconfdir}/wireplumber/"
    systemd_dir="${D}${sysconfdir}/systemd/system/pipewire.service.wants"

    install -d ${config_dir}
    install -m 0644 ${WORKDIR}/00-functions.lua ${config_dir}

    # config of the policy instance
    install -d ${config_dir}/policy.lua.d/
    ln -s ../00-functions.lua ${config_dir}/policy.lua.d/00-functions.lua
    install -m 0644 ${WORKDIR}/policy.lua.d/*.lua ${config_dir}/policy.lua.d/
    install -m 0644 ${WORKDIR}/policy.conf ${config_dir}

    # enable additional systemd services
    install -d ${systemd_dir}
    ln -s ${systemd_system_unitdir}/wireplumber@.service ${systemd_dir}/wireplumber@policy.service
}

FILES:${PN} += "\
    ${sysconfdir}/* \
    ${datadir}/wireplumber/* \
"
CONFFILES:${PN} += "\
    ${sysconfdir}/* \
"
