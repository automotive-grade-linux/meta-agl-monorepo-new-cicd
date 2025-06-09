SUMMARY = "CAN provider for KUKSA.val, the KUKSA Vehicle Abstraction Layer"
HOMEPAGE = "https://github.com/eclipse-kuksa"
BUGTRACKER = "https://github.com/eclips-kuksa/kuksa-can-provider/issues"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "python3-setuptools-git-versioning-native"

PV = "0.4.4"

SRC_URI = "git://github.com/eclipse-kuksa/kuksa-can-provider.git;protocol=https;branch=main \
           file://0001-dbc2val-add-installation-mechanism.patch \
           file://0002-dbc2val-usability-improvements.patch \
           file://0003-dbc2val-fix-token-file-configuration-option.patch \
           file://0004-Enable-val2dbc-for-sensor-values.patch \
           file://kuksa-can-provider.service \
	   file://kuksa-can-provider.default \
           "
SRCREV = "669bb122c599fb17afedec5a1a866bafca497cbd"

S = "${WORKDIR}/git"

inherit setuptools3 systemd update-alternatives

SYSTEMD_SERVICE:${PN} = "${BPN}.service"

do_install:append() {
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${WORKDIR}/kuksa-can-provider.default ${D}${sysconfdir}/default/
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 0644 ${WORKDIR}/kuksa-can-provider.service ${D}${systemd_system_unitdir}
    fi

    # Install tweaked copy of the example configuration, the .ini file
    # and the upstream demo/test files it references are packaged
    # separately from the library and daemon to make it easy to replace
    # them.
    install -d ${D}${sysconfdir}/kuksa-can-provider
    sed -e 's|^mapping =.*|mapping = /usr/share/vss/vss.json|' \
        -e 's|^dbcfile =.*|dbcfile = /usr/share/dbc/Model3CAN.dbc|' \
        -e 's|^candumpfile =.*|dbcfile = /usr/share/can/candump.log|' \
        -e 's|^dbc_default_file =.*|dbc_default_file = /etc/kuksa-can-provider/dbc_default_values.json|' \
	${S}/config/dbc_feeder.ini > ${D}${sysconfdir}/kuksa-can-provider/config.ini
    install -m 0644 ${S}/dbc_default_values.json ${D}${sysconfdir}/kuksa-can-provider/
    install -d ${D}${datadir}/dbc
    install -m 0644 ${S}/Model3CAN.dbc ${D}${datadir}/dbc/
    install -d ${D}${datadir}/can
    install -m 0644 ${S}/candump.log ${D}${datadir}/can/
}

ALTERNATIVE_LINK_NAME[kuksa-can-provider.env] = "${sysconfdir}/default/kuksa-can-provider"

FILES:${PN} += "${systemd_system_unitdir}"

# NOTE:
# Since the environment file is used by the systemd unit, it is packaged
# with the it in the main package, and the alternative scheme is set up
# against that. Replacement configuration packages can still configure
# the alternative as required.
ALTERNATIVE_TARGET_${PN} = "${sysconfdir}/default/kuksa-can-provider.default"

PACKAGE_BEFORE_PN += "${PN}-conf-example"

FILES:${PN}-conf-example += " \
    ${sysconfdir}/kuksa-can-provider/config.ini \
    ${sysconfdir}/kuksa-can-provider/dbc_default_values.json \
    ${datadir}/dbc/Model3CAN.dbc \
    ${datadir}/can/candump.log \
"
RPROVIDES:${PN}-conf-example = "kuksa-can-provider-conf"

RDEPENDS:${PN} += " \
    bash \
    python3-pyserial \
    python3-cantools \
    python3-can \
    python3-can-j1939 \
    python3-pyyaml \
    python3-py-expression-eval \
    kuksa-client \
    kuksa-can-provider-conf \
"
