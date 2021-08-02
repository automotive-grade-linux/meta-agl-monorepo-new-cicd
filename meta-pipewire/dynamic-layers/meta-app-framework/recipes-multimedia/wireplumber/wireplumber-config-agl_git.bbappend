FILESEXTRAPATHS:prepend := "${THISDIR}/wireplumber-config-agl:"

SRC_URI += "\
    file://50-access-agl.lua \
    file://access-smack.lua \
"

do_install:append() {
    # install smack-specific config
    config_dir="${D}${sysconfdir}/wireplumber/config.lua.d/"
    access_dir="${D}${sysconfdir}/wireplumber/scripts/access/"
    mkdir -p ${access_dir}
    install -m 0644 ${WORKDIR}/50-access-agl.lua ${config_dir}
    install -m 0644 ${WORKDIR}/access-smack.lua ${access_dir}
}
