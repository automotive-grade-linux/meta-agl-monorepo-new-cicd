FILESEXTRAPATHS:prepend := "${THISDIR}/pipewire:"

SRC_URI:append= "\
    file://0001-modules-add-new-access-seclabel-module.patch \
    file://pipewire.conf \
    file://pipewire.service \
    file://pipewire.socket \
    file://smack-pipewire \
"

do_install:append() {
     # replace the original config with our smack-aware config
    mkdir -p ${D}${sysconfdir}/pipewire/
    install -m 0644 ${WORKDIR}/pipewire.conf ${D}${sysconfdir}/pipewire/pipewire.conf

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        # remove the original unit files shipped by pipewire
        rm -rf ${D}${systemd_system_unitdir}/pipewire.*

        # install our own system-level templates
        mkdir -p ${D}${systemd_system_unitdir}/
        install -m 0644 ${WORKDIR}/pipewire.service ${D}${systemd_system_unitdir}/pipewire.service
        install -m 0644 ${WORKDIR}/pipewire.socket ${D}${systemd_system_unitdir}/pipewire.socket

        # install smack rules
        mkdir -p ${D}${sysconfdir}/smack/accesses.d
        install -m 0644 ${WORKDIR}/smack-pipewire ${D}${sysconfdir}/smack/accesses.d/pipewire
    fi
}

FILES:${PN}:append = "\
    ${sysconfdir}/smack/accesses.d/* \
    ${sysconfdir}/pipewire/pipewire.conf \
"
