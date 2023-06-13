SUMMARY = "Startup systemd unit for the AGL Wayland compositor"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit systemd

# Reuse include file from upstream weston since we have the same requirements
require recipes-graphics/wayland/required-distro-features.inc

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "file://agl-compositor.service \
           file://agl-compositor.socket \
           file://agl-compositor-autologin \
           file://agl-compositor.conf.in \
"

S = "${WORKDIR}"

AGL_COMPOSITOR_ARGS ?= " --config ${sysconfdir}/xdg/weston/weston.ini --idle-time=0"
AGL_COMPOSITOR_USE_PIXMAN ??= "0"

AGL_COMPOSITOR_ARGS:append = " ${@bb.utils.contains("DISTRO_FEATURES", "agl-devel", " --debug", "",d)}"
AGL_COMPOSITOR_ARGS:append = " ${@bb.utils.contains("WESTON_USE_PIXMAN", "1", " --use-pixman", "",d)}"

do_install() {
    # Install systemd service
    install -D -p -m0644 ${WORKDIR}/agl-compositor.service ${D}${systemd_system_unitdir}/agl-compositor.service
    install -D -p -m0644 ${WORKDIR}/agl-compositor.socket ${D}${systemd_system_unitdir}/agl-compositor.socket
    if [ "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}" ]; then
        install -D -p -m0644 ${WORKDIR}/agl-compositor-autologin ${D}${sysconfdir}/pam.d/agl-compositor-autologin
    fi

    # Install systemd service drop-in with extra configuration
    files=agl-compositor.conf.in
    for f in ${files}; do
        g=${f%.in}
        if [ "${f}" != "${g}" ]; then
            sed -e "s,@AGL_COMPOSITOR_ARGS@,${AGL_COMPOSITOR_ARGS},g" \
                ${WORKDIR}/${f} > ${WORKDIR}/${g}
        fi
    done
    install -d ${D}${systemd_system_unitdir}/agl-compositor.service.d
    install -m644 ${WORKDIR}/agl-compositor.conf ${D}/${systemd_system_unitdir}/agl-compositor.service.d/agl-compositor.conf
}

FILES:${PN} += "\
    ${systemd_system_unitdir}/agl-compositor.service \
    ${systemd_system_unitdir}/agl-compositor.socket \
    ${systemd_system_unitdir}/agl-compositor.service.d \
    ${sysconfdir}/default/agl-compositor \
    ${sysconfdir}/pam.d/ \
    "

CONFFILES:${PN} += "${sysconfdir}/default/agl-compositor"

RDEPENDS:${PN} = "agl-users agl-compositor weston-ini"

RCONFLICTS:${PN} = "weston-init"

SYSTEMD_SERVICE:${PN} = "agl-compositor.service agl-compositor.socket"
