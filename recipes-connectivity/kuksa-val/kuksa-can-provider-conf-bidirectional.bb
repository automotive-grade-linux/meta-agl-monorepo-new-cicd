SUMMARY = "KUKSA.val CAN provider configuration for bidirectional VSS-CAN"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "file://kuksa-can-provider.bidirectional"

S = "${WORKDIR}"

inherit allarch update-alternatives

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sysconfdir}/default
    install -m 0644 ${WORKDIR}/kuksa-can-provider.bidirectional ${D}${sysconfdir}/default/
}

ALTERNATIVE_LINK_NAME[kuksa-can-provider.env] = "${sysconfdir}/default/kuksa-can-provider"

RPROVIDES:${PN} = "kuksa-can-provider.env"
ALTERNATIVE:${PN} = "kuksa-can-provider.env"
ALTERNATIVE_TARGET_${PN} = "${sysconfdir}/default/kuksa-can-provider.bidirectional"
ALTERNATIVE_PRIORITY_${PN} = "21"

RDEPENDS:${PN} += "kuksa-can-provider kuksa-can-provider-conf-agl"
