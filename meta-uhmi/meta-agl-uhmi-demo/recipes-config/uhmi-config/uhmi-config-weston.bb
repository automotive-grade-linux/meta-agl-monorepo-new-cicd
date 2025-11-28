SUMMARY     = "Setting files for UHMI sender"
LICENSE     = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = " \
    file://ucl-node.conf \
    file://ucl-lifecycle-manager.conf \
    file://ula-node.conf \
    file://ula-client-manager.conf \
"

# This package is usually board/setup specific
PACKAGE_ARCH = "${MACHINE_ARCH}"

# No actions taken during configure and compile
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}/etc/systemd/system/ucl-node.service.d/
    install -m 0644 ${WORKDIR}/ucl-node.conf ${D}/etc/systemd/system/ucl-node.service.d/

    install -d ${D}/etc/systemd/system/ucl-lifecycle-manager.service.d/
    install -m 0644 ${WORKDIR}/ucl-lifecycle-manager.conf ${D}/etc/systemd/system/ucl-lifecycle-manager.service.d/

    install -d ${D}/etc/systemd/system/ula-node.service.d/
    install -m 0644 ${WORKDIR}/ula-node.conf ${D}/etc/systemd/system/ula-node.service.d/

    install -d ${D}/etc/systemd/system/ula-client-manager.service.d/
    install -m 0644 ${WORKDIR}/ula-client-manager.conf ${D}/etc/systemd/system/ula-client-manager.service.d/
}
