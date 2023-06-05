SUMMARY = "Configuration file for the Weston and AGL Wayland compositors"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
	file://core.cfg \
	file://shell.cfg \
	file://hdmi-a-1-0.cfg \
	file://hdmi-a-1-90.cfg \
	file://hdmi-a-1-180.cfg \
	file://hdmi-a-1-270.cfg \
	file://hdmi-a-2-0.cfg \
	file://hdmi-a-2-90.cfg \
	file://hdmi-a-2-180.cfg \
	file://hdmi-a-2-270.cfg \
	file://remote-output.cfg.in \
	file://transmitter-output.cfg.in \
	file://virtual-0.cfg \
	file://virtual-90.cfg \
	file://virtual-180.cfg \
	file://virtual-270.cfg \
	file://grpc-proxy.cfg \
"

S = "${WORKDIR}"

inherit update-alternatives

# Default primary display/orientation fragment
WESTON_DISPLAYS ?= "hdmi-a-1-90"

# Configuration fragments to use in weston.ini.*
# Note that some may be replaced/removed when building the landscape
# configuration.
WESTON_FRAGMENTS = "core shell ${WESTON_DISPLAYS}"

# On-target weston.ini directory
weston_ini_dir = "${sysconfdir}/xdg/weston"

# Options for the user to change in local.conf
# e.g. TRANSMITTER_OUTPUT_MODE = "1080x1488"
TRANSMITTER_OUTPUT_MODE ??= "640x720@30"
TRANSMITTER_OUTPUT_HOST ??= "192.168.10.3"
TRANSMITTER_OUTPUT_PORT ??= "5005"

do_configure() {
    sed -e "s#mode=.*#mode=${TRANSMITTER_OUTPUT_MODE}#" \
        -e "s#host=.*#host=${TRANSMITTER_OUTPUT_HOST}#" \
        -e "s#port=.*#port=${TRANSMITTER_OUTPUT_PORT}#" \
        ${WORKDIR}/transmitter-output.cfg.in  > ${WORKDIR}/transmitter-output.cfg
    sed -e "s#host=.*#host=${TRANSMITTER_OUTPUT_HOST}#" \
        -e "s#port=.*#port=${TRANSMITTER_OUTPUT_PORT}#" \
        ${WORKDIR}/remote-output.cfg.in  > ${WORKDIR}/remote-output.cfg
}

do_compile() {
    # Put all of our cfg files together for a default portrait
    # orientation configuration
    rm -f ${WORKDIR}/weston.ini.default
    for F in ${WESTON_FRAGMENTS}; do
        cat ${WORKDIR}/${F}.cfg >> ${WORKDIR}/weston.ini.default
        echo >> ${WORKDIR}/weston.ini.default
    done
    sed -i -e '$ d' ${WORKDIR}/weston.ini.default

    cat ${WORKDIR}/weston.ini.default > ${WORKDIR}/weston.ini.default-no-activate
    sed -i -e 's#\[core\]#[core]\nactivate-by-default=false#' ${WORKDIR}/weston.ini.default-no-activate
    cat ${WORKDIR}/grpc-proxy.cfg >> ${WORKDIR}/weston.ini.default-no-activate

    # Do it again, but filter fragments to configure for landscape
    # and a corresponding landscape-inverted that is 180 degrees
    # rotated.
    rm -f ${WORKDIR}/weston.ini.landscape
    rm -f ${WORKDIR}/weston.ini.landscape-inverted
    for F in ${WESTON_FRAGMENTS}; do
        INVF=$F
        if echo $F | grep '^hdmi-a-1-\(90\|270\)$'; then
            F="hdmi-a-1-0"
            INVF="hdmi-a-1-180"
        elif echo $F | grep '^hdmi-a-2-\(90\|270\)$'; then
            F="hdmi-a-2-0"
            INVF="hdmi-a-2-180"
        elif echo $F | grep '^virtual-90$'; then
            F="virtual-0"
            INVF="virtual-180"
        fi
        cat ${WORKDIR}/${F}.cfg >> ${WORKDIR}/weston.ini.landscape
        cat ${WORKDIR}/${INVF}.cfg >> ${WORKDIR}/weston.ini.landscape-inverted
        echo >> ${WORKDIR}/weston.ini.landscape
        echo >> ${WORKDIR}/weston.ini.landscape-inverted
    done
    sed -i -e '$ d' ${WORKDIR}/weston.ini.landscape
    sed -i -e '$ d' ${WORKDIR}/weston.ini.landscape-inverted

    cat ${WORKDIR}/weston.ini.landscape > ${WORKDIR}/weston.ini.landscape-no-activate
    sed -i -e 's#\[core\]#[core]\nactivate-by-default=false#' ${WORKDIR}/weston.ini.landscape-no-activate
    cat ${WORKDIR}/grpc-proxy.cfg >> ${WORKDIR}/weston.ini.landscape-no-activate
}

do_install:append() {
    install -d ${D}${weston_ini_dir}
    install -m 0644 ${WORKDIR}/weston.ini.default ${D}${weston_ini_dir}/
    install -m 0644 ${WORKDIR}/weston.ini.default-no-activate ${D}${weston_ini_dir}/
    install -m 0644 ${WORKDIR}/weston.ini.landscape-no-activate ${D}${weston_ini_dir}/
    install -m 0644 ${WORKDIR}/weston.ini.landscape ${D}${weston_ini_dir}/
    install -m 0644 ${WORKDIR}/weston.ini.landscape-inverted ${D}${weston_ini_dir}/
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

# Use the alternative mechanism to handle multiple packages providing
# weston.ini.  This seems simpler than other possible approaches.
# Note that for now the generated packages are being marked as
# incompatible with each other for simplicity, that can be changed if
# a usecase where switching between alternatives at runtime is desirable
# appears.

ALTERNATIVE_LINK_NAME[weston.ini] = "${weston_ini_dir}/weston.ini"

RPROVIDES:${PN} = "weston-ini"
RCONFLICTS:${PN} = "${PN}-landscape"
ALTERNATIVE:${PN} = "weston.ini"
ALTERNATIVE_TARGET_${PN} = "${weston_ini_dir}/weston.ini.default"

PACKAGE_BEFORE_PN += "${PN}-landscape"

FILES:${PN}-landscape = "${weston_ini_dir}/weston.ini.landscape"

RPROVIDES:${PN}-landscape = "weston-ini"
RCONFLICTS:${PN}-landscape = "${PN}"
ALTERNATIVE:${PN}-landscape = "weston.ini"
ALTERNATIVE_TARGET_${PN}-landscape = "${weston_ini_dir}/weston.ini.landscape"

PACKAGE_BEFORE_PN += "${PN}-landscape-inverted"

FILES:${PN}-landscape-inverted = "${weston_ini_dir}/weston.ini.landscape-inverted"

RPROVIDES:${PN}-landscape-inverted = "weston-ini"
RCONFLICTS:${PN}-landscape-inverted = "${PN}"
ALTERNATIVE:${PN}-landscape-inverted = "weston.ini"
ALTERNATIVE_TARGET_${PN}-landscape-inverted = "${weston_ini_dir}/weston.ini.landscape-inverted"

# no activation by default
PACKAGE_BEFORE_PN += "${PN}-no-activate"

FILES:${PN}-no-activate = "${weston_ini_dir}/weston.ini.default-no-activate"

RPROVIDES:${PN}-no-activate = "weston-ini"
RCONFLICTS:${PN}-no-activate = "${PN}"
ALTERNATIVE:${PN}-no-activate = "weston.ini"
ALTERNATIVE_TARGET_${PN}-no-activate = "${weston_ini_dir}/weston.ini.default-no-activate"

# landscape, no activation by default
PACKAGE_BEFORE_PN += "${PN}-landscape-no-activate"

FILES:${PN}-landscape-no-activate = "${weston_ini_dir}/weston.ini.landscape-no-activate"

RPROVIDES:${PN}-landscape-no-activate = "weston-ini"
RCONFLICTS:${PN}-landscape-no-activate = "${PN}"
ALTERNATIVE:${PN}-landscape-no-activate = "weston.ini"
ALTERNATIVE_TARGET_${PN}-landscape-no-activate = "${weston_ini_dir}/weston.ini.landscape-no-activate"

# This is a settings-only package, we do not need a development package
# (and its fixed dependency to ${PN} being installed)
PACKAGES:remove = "${PN}-dev ${PN}-staticdev"
