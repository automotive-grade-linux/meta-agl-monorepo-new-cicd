SUMMARY = "StarFive VisionFive 2 U-Boot environment file"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "file://vf2_uEnv.txt"

S = "${UNPACKDIR}"

COMPATIBLE_MACHINE = "visionfive2"

inherit deploy

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

do_deploy () {
    install -D -m 644 ${UNPACKDIR}/vf2_uEnv.txt ${DEPLOYDIR}/vf2_uEnv.txt
}

addtask deploy before do_build after do_compile
