FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://agl-compositor-rockchip.conf "

do_install:append() {
    install -m644 ${UNPACKDIR}/agl-compositor-rockchip.conf ${D}/${systemd_system_unitdir}/agl-compositor.service.d/agl-compositor-rockchip.conf
}
