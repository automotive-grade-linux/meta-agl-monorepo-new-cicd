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
	file://remote-output.cfg \
	file://transmitter-output.cfg \
	file://virtual-0.cfg \
	file://virtual-180.cfg \
	file://virtual-270.cfg \
"

S = "${WORKDIR}"

inherit update-alternatives

# Default primary display/orientation fragment
WESTON_DISPLAYS ?= "hdmi-a-1-270"

# Configuration fragments to use in weston.ini.*
# Note that some may be replaced/removed when building the landscape
# configuration.
WESTON_FRAGMENTS = "core shell ${WESTON_DISPLAYS}"

# On-target weston.ini directory
weston_ini_dir = "${sysconfdir}/xdg/weston"

do_compile() {
    # Put all of our cfg files together for a default portrait
    # orientation configuration
    rm -f ${WORKDIR}/weston.ini.default
    for F in ${WESTON_FRAGMENTS}; do
        cat ${WORKDIR}/${F}.cfg >> ${WORKDIR}/weston.ini.default
        echo >> ${WORKDIR}/weston.ini.default
    done
    sed -i -e '$ d' ${WORKDIR}/weston.ini.default

    # Do it again, but filter fragments to configure for landscape
    # and a corresponding landscape-inverted that is 180 degrees
    # rotated.
    rm -f ${WORKDIR}/weston.ini.landscape
    for F in ${WESTON_FRAGMENTS}; do
        INVF=$F
        if echo $F | grep '^hdmi-a-1-\(90\|270\)$'; then
            F="hdmi-a-1-0"
            INVF="hdmi-a-1-180"
        elif echo $F | grep '^virtual-270$'; then
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
}

do_install_append() {
    install -d ${D}${weston_ini_dir}
    install -m 0644 ${WORKDIR}/weston.ini.default ${D}${weston_ini_dir}/
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

RDEPENDS_${PN} = "weston-init"
RPROVIDES_${PN} = "weston-ini"
RCONFLICTS_${PN} = "${PN}-landscape"
ALTERNATIVE_${PN} = "weston.ini"
ALTERNATIVE_TARGET_${PN} = "${weston_ini_dir}/weston.ini.default"

PACKAGE_BEFORE_PN += "${PN}-landscape"

FILES_${PN}-landscape = "${weston_ini_dir}/weston.ini.landscape"

RDEPENDS_${PN}-landscape = "weston-init"
RPROVIDES_${PN}-landscape = "weston-ini"
RCONFLICTS_${PN}-landscape = "${PN}"
ALTERNATIVE_${PN}-landscape = "weston.ini"
ALTERNATIVE_TARGET_${PN}-landscape = "${weston_ini_dir}/weston.ini.landscape"

PACKAGE_BEFORE_PN += "${PN}-landscape-inverted"

FILES_${PN}-landscape-inverted = "${weston_ini_dir}/weston.ini.landscape-inverted"

RDEPENDS_${PN}-landscape-inverted = "weston-init"
RPROVIDES_${PN}-landscape-inverted = "weston-ini"
RCONFLICTS_${PN}-landscape-inverted = "${PN}"
ALTERNATIVE_${PN}-landscape-inverted = "weston.ini"
ALTERNATIVE_TARGET_${PN}-landscape-inverted = "${weston_ini_dir}/weston.ini.landscape-inverted"
