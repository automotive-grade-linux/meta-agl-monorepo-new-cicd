SUMMARY = "Vehicle Signal Specification with AGL overlays"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"

DEPENDS = "vss-tools-native python3-pydantic-core-native python3-annotated-types-native"

inherit allarch update-alternatives

require recipes-support/vss/vss.inc

SRC_URI += "file://agl_vss_overlay.vspec \
            file://agl_vss_overlay.vspec.control-panel \
            file://agl_vss_overlay.vspec.control-panel-all \
            file://agl_vss_overlay.vspec.gw-single-can \
            file://agl_vss_overlay.vspec.gw-control-panel \
            file://agl_vss_overlay.vspec.gw-hardware \
"
# Since we're not relying on the simple upstream repo Makefile, use
# best practices and output into a separate directory.
B = "${WORKDIR}/build"

do_configure[noexec] = "1"

VSPEC_JSON_OPTS = " \
    -I ${S}/spec \
    -u ${S}/spec/units.yaml \
    --vspec ${S}/spec/VehicleSignalSpecification.vspec \
    -e dbc2vss \
    -e vss2dbc \
    --pretty \
"

do_compile() {
    vspec export json ${VSPEC_JSON_OPTS} -l ${WORKDIR}/agl_vss_overlay.vspec --output vss_${PV}-agl.json
    vspec export json ${VSPEC_JSON_OPTS} -l ${WORKDIR}/agl_vss_overlay.vspec.control-panel --output vss_${PV}-agl-control-panel.json
    vspec export json ${VSPEC_JSON_OPTS} -l ${WORKDIR}/agl_vss_overlay.vspec.control-panel-all --output vss_${PV}-agl-control-panel-all.json
    vspec export json ${VSPEC_JSON_OPTS} -l ${WORKDIR}/agl_vss_overlay.vspec.gw-single-can --output vss_${PV}-agl-gw-single-can.json
    vspec export json ${VSPEC_JSON_OPTS} -l ${WORKDIR}/agl_vss_overlay.vspec.gw-control-panel --output vss_${PV}-agl-gw-control-panel.json
    vspec export json ${VSPEC_JSON_OPTS} -l ${WORKDIR}/agl_vss_overlay.vspec.gw-hardware --output vss_${PV}-agl-gw-hardware.json
}

do_install() {
    install -d ${D}${datadir}/vss
    install -m 0644 vss_${PV}-agl.json ${D}${datadir}/vss/
    install -m 0644 vss_${PV}-agl-control-panel.json ${D}${datadir}/vss/
    install -m 0644 vss_${PV}-agl-control-panel-all.json ${D}${datadir}/vss/
    install -m 0644 vss_${PV}-agl-gw-single-can.json ${D}${datadir}/vss/
    install -m 0644 vss_${PV}-agl-gw-control-panel.json ${D}${datadir}/vss/
    install -m 0644 vss_${PV}-agl-gw-hardware.json ${D}${datadir}/vss/
}

PACKAGE_BEFORE_PN += "${PN}-control-panel ${PN}-control-panel-all ${PN}-gw-single-can ${PN}-gw-control-panel ${PN}-gw-hardware"

ALTERNATIVE_LINK_NAME[vss.json] = "${datadir}/vss/vss.json"

ALTERNATIVE:${PN} = "vss.json"
ALTERNATIVE_TARGET_${PN} = "${datadir}/vss/vss_${PV}-agl.json"
ALTERNATIVE_PRIORITY_${PN} = "20"
FILES:${PN} += "${datadir}/vss/vss_${PV}-agl.json"

# Support CAN output for all IC page signals
ALTERNATIVE:${PN}-control-panel = "vss.json"
ALTERNATIVE_TARGET_${PN}-control-panel = "${datadir}/vss/vss_${PV}-agl-control-panel.json"
ALTERNATIVE_PRIORITY_${PN}-control-panel = "30"
FILES:${PN}-control-panel += "${datadir}/vss/vss_${PV}-agl-control-panel.json"

# Support CAN output for all control panel pages, input for HVAC
ALTERNATIVE:${PN}-control-panel-all = "vss.json"
ALTERNATIVE_TARGET_${PN}-control-panel-all = "${datadir}/vss/vss_${PV}-agl-control-panel-all.json"
ALTERNATIVE_PRIORITY_${PN}-control-panel-all = "31"
FILES:${PN}-control-panel-all += "${datadir}/vss/vss_${PV}-agl-control-panel-all.json"

# CAN input of all signals, output of HVAC
ALTERNATIVE:${PN}-gw-single-can = "vss.json"
ALTERNATIVE_TARGET_${PN}-gw-single-can = "${datadir}/vss/vss_${PV}-agl-gw-single-can.json"
ALTERNATIVE_PRIORITY_${PN}-gw-single-can = "32"
FILES:${PN}-gw-single-can += "${datadir}/vss/vss_${PV}-agl-gw-single-can.json"

# CAN input for steering wheel, output of HVAC
ALTERNATIVE:${PN}-gw-hardware = "vss.json"
ALTERNATIVE_TARGET_${PN}-gw-hardware = "${datadir}/vss/vss_${PV}-agl-gw-hardware.json"
ALTERNATIVE_PRIORITY_${PN}-gw-hardware = "35"
FILES:${PN}-gw-hardware += "${datadir}/vss/vss_${PV}-agl-gw-hardware.json"

# CAN input for demo control panel IC page signals
# Higher priority than gw-hardware, as we want it to be the default
# if both are installed (as is the case with the full gateway demo
# setup).
ALTERNATIVE:${PN}-gw-control-panel = "vss.json"
ALTERNATIVE_TARGET_${PN}-gw-control-panel = "${datadir}/vss/vss_${PV}-agl-gw-control-panel.json"
ALTERNATIVE_PRIORITY_${PN}-gw-control-panel = "36"
FILES:${PN}-gw-control-panel += "${datadir}/vss/vss_${PV}-agl-gw-control-panel.json"
