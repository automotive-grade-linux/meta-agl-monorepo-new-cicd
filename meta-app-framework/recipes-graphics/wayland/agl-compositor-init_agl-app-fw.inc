FILESEXTRAPATHS:prepend := "${THISDIR}/agl-compositor-init:"

SRC_URI = " \
    file://agl-compositor.conf \
    file://agl-compositor.service.in \
    file://agl-compositor.socket \
    "

# Reset systemd services list
SYSTEMD_SERVICE:${PN} = ""

do_install() {
    # Process ".in" files
    files=agl-compositor.service.in
    for f in ${files}; do
        g=${f%.in}
        if [ "${f}" != "${g}" ]; then
            sed -e "s,@AGL_COMPOSITOR_ARGS@,${AGL_COMPOSITOR_ARGS},g" \
                ${WORKDIR}/${f} > ${WORKDIR}/${g}
        fi
    done

    install -d ${D}${systemd_system_unitdir}/agl-session@agl-driver.service.d
    install -m 0644 ${WORKDIR}/agl-compositor.conf \
        ${D}${systemd_system_unitdir}/agl-session@agl-driver.service.d/agl-compositor.conf

    install -D -p -m0644 ${WORKDIR}/agl-compositor.service ${D}${systemd_user_unitdir}/agl-compositor.service
    install -D -p -m0644 ${WORKDIR}/agl-compositor.socket ${D}${systemd_user_unitdir}/agl-compositor.socket

    # Enable the compositor as part of the user session
    install -d ${D}${systemd_user_unitdir}/agl-session.target.wants
    ln -s ../agl-compositor.service ${D}${systemd_user_unitdir}/agl-session.target.wants/agl-compositor.service
}

FILES:${PN} = "\
    ${systemd_system_unitdir} \
    ${systemd_user_unitdir} \
"

RDEPENDS:${PN} = " \
    agl-compositor \
    agl-session \
    weston-ini-conf \
"

RCONFLICTS:${PN} = "weston-init"
