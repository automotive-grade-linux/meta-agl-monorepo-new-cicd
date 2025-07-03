SUMMARY = "Unified HMI Clustering Tools"
SECTION = "graphics"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://src/ucl-tools/LICENSE.md;md5=e789951aab02a3028d2e58b90fc933ba"

FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

PN="ucl-tools"
PROVIDES += "ucl-tools"

SRCREV = "2e779ffa5d0042ae61beb690934a18eadab3934e"
BRANCH ?= "main"
SRC_URI = " \
    git://github.com/unified-hmi/ucl-tools.git;protocol=https;branch=${BRANCH} \
"
PV = "0.0+git${SRCPV}"

export GO111MODULE="auto"
export GOFLAGS="-modcacherw"

GO_IMPORT = "ucl-tools"
GO_INSTALL = " \
    ${GO_IMPORT}/cmd/ucl-node \
    ${GO_IMPORT}/cmd/ucl-lifecycle-manager \
    ${GO_IMPORT}/cmd/ucl-api-comm \
    ${GO_IMPORT}/cmd/ucl-virtio-gpu-wl-send \
    ${GO_IMPORT}/cmd/ucl-virtio-gpu-wl-recv \
    ${GO_IMPORT}/cmd/ucl-virtio-gpu-rvgpu-compositor \
"

inherit go

RDEPENDS:${PN}  = "bash"
RDEPENDS:${PN}-dev = "bash"

inherit systemd features_check
SRC_URI += " \
    file://ucl-node.service \
    file://ucl-lifecycle-manager.service \
"

REQUIRED_DISTRO_FEATURES = "systemd"
SYSTEMD_SERVICE:${PN} = "ucl-node.service ucl-lifecycle-manager.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_compile[network] = "1"
do_compile:prepend() {
    export http_proxy=${http_proxy}
    export https_proxy=${https_proxy}
    cd ${GOPATH}/src/ucl-tools
    oe_runmake mod
}

do_compile:append() {
    export CGO_ENABLED="1"
    export GOFLAGS="-trimpath"
    ${GO} build  -buildmode=c-shared -o ${GOPATH}/pkg/libuclclient.so -v -ldflags '-extldflags "-Wl,-soname=libuclclient.so"' ${GO_IMPORT}/pkg/ucl-client-lib
}

do_install() {
    if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
        install -d ${D}${systemd_system_unitdir}
        install -m 644 ${UNPACKDIR}/*.service ${D}/${systemd_system_unitdir}
    fi

    install -d ${D}${bindir}
    install -m 0755 ${B}/${GO_BUILD_BINDIR}/* ${D}${bindir}

    install -d ${D}${libdir}
    install -m 0755 ${B}/pkg/libuclclient.so ${D}${libdir}

    install -d ${D}${includedir}
    install -m 644 ${B}/pkg/libuclclient.h ${D}${includedir}
}

FILES:${PN} += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_system_unitdir}/${SYSTEMD_SERVICE}', '', d)} \
    ${libdir} \
    ${includedir} \
"
