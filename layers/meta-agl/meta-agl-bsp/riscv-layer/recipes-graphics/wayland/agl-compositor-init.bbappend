FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:jh7110 = " file://agl-compositor-visionfive2.conf "

do_install:append:jh7110() {
    install -m644 ${UNPACKDIR}/agl-compositor-visionfive2.conf ${D}/${systemd_system_unitdir}/agl-compositor.service.d/agl-compositor-visionfive2.conf
}
