SUMMARY = "Startup systemd unit drop-in file for the AGL Wayland compositor"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "file://agl-compositor.conf.in"

S = "${WORKDIR}"

AGL_COMPOSITOR_ARGS ?= " --config ${sysconfdir}/xdg/weston/weston.ini --idle-time=0"
AGL_COMPOSITOR_USE_PIXMAN ??= "0"

AGL_COMPOSITOR_ARGS:append = " ${@bb.utils.contains("DISTRO_FEATURES", "agl-devel", " --debug", "",d)}"
AGL_COMPOSITOR_ARGS:append = " ${@bb.utils.contains("WESTON_USE_PIXMAN", "1", " --use-pixman", "",d)}"

do_install() {
    # Process ".in" files
    files=agl-compositor.conf.in
    for f in ${files}; do
        g=${f%.in}
        if [ "${f}" != "${g}" ]; then
            sed -e "s,@AGL_COMPOSITOR_ARGS@,${AGL_COMPOSITOR_ARGS},g" \
                ${WORKDIR}/${f} > ${WORKDIR}/${g}
        fi
    done

    # Install Weston systemd service drop-in
    install -d ${D}${systemd_system_unitdir}/weston.service.d
    install -m644 ${WORKDIR}/agl-compositor.conf ${D}/${systemd_system_unitdir}/weston.service.d/agl-compositor.conf
}

FILES:${PN} += "\
    ${systemd_system_unitdir}/weston.service.d \
    "

RDEPENDS:${PN} = " \
    agl-compositor \
    weston-init \
"
