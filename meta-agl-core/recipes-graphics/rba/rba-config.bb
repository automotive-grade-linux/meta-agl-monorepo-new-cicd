SUMMARY = "Rule Based Arbitrator Model required for RBA policy compositor."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "file://RBAModel.json"

S = "${WORKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install_append() {
    install -d ${D}/${sysconfdir}/rba
    install -m 0644 ${WORKDIR}/RBAModel.json ${D}/${sysconfdir}/rba
}
