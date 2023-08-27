FILESEXTRAPATHS:prepend := "${THISDIR}/agl-compositor-init-agl-container-guest:"

SRC_URI:append = " \
    file://agl-compositor.env \
    file://agl-compositor-guest.conf \
    "

DRM_LEASE_DEVICE = "lease"

do_install:append() {
    install -Dm644 ${WORKDIR}/agl-compositor.env ${D}${sysconfdir}/default/agl-compositor
    install -m644 ${WORKDIR}/agl-compositor-guest.conf ${D}/${systemd_system_unitdir}/agl-compositor.service.d/agl-compositor-guest.conf
}

FILES:${PN} += "\
    ${sysconfdir}/default/agl-compositor \
    "
