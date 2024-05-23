SUMMARY     = "AGL configuration file for wireplumber policy"
HOMEPAGE    = "https://gitlab.freedesktop.org/gkiagia/wireplumber"
BUGTRACKER  = "https://jira.automotivelinux.org"
AUTHOR      = "Ashok Sidipotu <ashok.sidipotu@collabora.com>"
SECTION     = "multimedia"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://50-AGL-equalizer.conf \
    file://50-AGL-media-role-nodes.conf \
"

S = "${UNPACKDIR}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install:append() {
    config_dir="${D}${sysconfdir}/wireplumber/wireplumber.conf.d/"
    systemd_dir="${D}${sysconfdir}/systemd/system/pipewire.service.wants"

    install -d ${config_dir}
    install -m 0644 ${UNPACKDIR}/50-AGL-equalizer.conf ${config_dir}
    install -m 0644 ${UNPACKDIR}/50-AGL-media-role-nodes.conf ${config_dir}

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
