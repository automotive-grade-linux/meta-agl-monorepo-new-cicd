SUMMARY = "Unified HMI Layout Tools"
SECTION = "graphics"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/ula-tools/LICENSE.md;md5=e789951aab02a3028d2e58b90fc933ba"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

PN="ula-tools"
PROVIDES += "ula-tools"

SRCREV = "82778493c7c8b4d5ba539e62625f063b4239e364"
BRANCH ?= "main"
SRC_URI = " \
    git://github.com/unified-hmi/ula-tools.git;protocol=https;branch=${BRANCH} \
"
PV = "0.0+git${SRCPV}"

export GO111MODULE="auto"
export GOFLAGS="-modcacherw"

GO_IMPORT = "ula-tools"
GO_INSTALL = " ${GO_IMPORT}/cmd/ula-node  ${GO_IMPORT}/cmd/ula-client-manager ${GO_IMPORT}/cmd/ula-grpc-client "

inherit go
RDEPENDS:${PN}  = "jq bash"
RDEPENDS:${PN}-dev  = "bash"

inherit systemd features_check

SRC_URI += " \
	file://ula-node.service \
	file://ula-client-manager.service \
	file://virtual-screen-def.json \
	"

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "ula-node.service ula-client-manager.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_compile[network] = "1"
do_compile:prepend() {
    export http_proxy=${http_proxy}
    export https_proxy=${https_proxy}
    cd ${GOPATH}/src/ula-tools
    oe_runmake mod
}

do_compile:append() {
    export CGO_ENABLED="1"
    export GOFLAGS="-trimpath"
    ${GO} build  -buildmode=c-shared -o ${GOPATH}/pkg/libulaclient.so -v -ldflags '-extldflags "-Wl,-soname=libulaclient.so"' ${GO_IMPORT}/pkg/ula-client-lib
}

do_install() {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 644 ${UNPACKDIR}/*.service ${D}/${systemd_system_unitdir}
    fi

    install -d ${D}${bindir}
    install -m 0755 ${B}/${GO_BUILD_BINDIR}/* ${D}${bindir}

    install -d ${D}/etc/uhmi-framework
    install -m 644 ${UNPACKDIR}/virtual-screen-def.json ${D}/etc/uhmi-framework

    install -d ${D}${libdir}
    install -m 0755 ${B}/pkg/libulaclient.so ${D}${libdir}

    install -d ${D}${includedir}
    install -m 644 ${B}/pkg/libulaclient.h ${D}${includedir}
}

FILES:${PN} += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_system_unitdir}/${SYSTEMD_SERVICE}', '', d)} \
    /etc/uhmi-framework/virtual-screen-def.json \
    ${libdir} \
    ${includedir} \
    "
