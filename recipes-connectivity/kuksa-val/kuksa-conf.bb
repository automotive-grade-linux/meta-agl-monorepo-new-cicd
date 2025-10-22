SUMMARY = "AGL demo application KUKSA.val configuration file"
HOMEPAGE = "https://github.com/eclipse/kuksa.val"
BUGTRACKER = "https://github.com/eclipse/kuksa.val/issues"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

SRC_URI = "file://kuksa.toml.default \
           file://kuksa.toml.demo-preconfigured \
           file://kuksa.toml.gateway-demo \
           file://kuksa.toml.kvm-demo \
           file://kuksa.toml.kvm-demo-preconfigured \
"

inherit allarch update-alternatives

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sysconfdir}/xdg/AGL
    install -m 0644 ${WORKDIR}/kuksa.toml.default ${D}${sysconfdir}/xdg/AGL/
    install -m 0644 ${WORKDIR}/kuksa.toml.demo-preconfigured ${D}${sysconfdir}/xdg/AGL/
    install -m 0644 ${WORKDIR}/kuksa.toml.gateway-demo ${D}${sysconfdir}/xdg/AGL/
    install -m 0644 ${WORKDIR}/kuksa.toml.kvm-demo ${D}${sysconfdir}/xdg/AGL/
    install -m 0644 ${WORKDIR}/kuksa.toml.kvm-demo-preconfigured ${D}${sysconfdir}/xdg/AGL/
}

ALTERNATIVE_LINK_NAME[kuksa-toml] = "${sysconfdir}/xdg/AGL/kuksa.toml"

RDEPENDS:${PN} = "kuksa-certificates-agl-ca"
RPROVIDES:${PN} = "kuksa-toml"
ALTERNATIVE:${PN} = "kuksa-toml"
ALTERNATIVE_TARGET_${PN} = "${sysconfdir}/xdg/AGL/kuksa.toml.default"

PACKAGE_BEFORE_PN += "${PN}-demo-preconfigured"
FILES:${PN}-demo-preconfigured += "${sysconfdir}/xdg/AGL/kuksa.toml.demo-preconfigured"
RDEPENDS:${PN}-demo-preconfigured = "kuksa-certificates-agl-ca"
RPROVIDES:${PN}-demo-preconfigured = "kuksa-toml"
ALTERNATIVE:${PN}-demo-preconfigured = "kuksa-toml"
ALTERNATIVE_TARGET_${PN}-demo-preconfigured = "${sysconfdir}/xdg/AGL/kuksa.toml.demo-preconfigured"

PACKAGE_BEFORE_PN += "${PN}-gateway-demo"
FILES:${PN}-gateway-demo += "${sysconfdir}/xdg/AGL/kuksa.toml.gateway-demo"
RDEPENDS:${PN}-gateway-demo = "kuksa-certificates-agl-ca"
RPROVIDES:${PN}-gateway-demo = "kuksa-toml"
ALTERNATIVE:${PN}-gateway-demo = "kuksa-toml"
ALTERNATIVE_TARGET_${PN}-gateway-demo = "${sysconfdir}/xdg/AGL/kuksa.toml.gateway-demo"

PACKAGE_BEFORE_PN += "${PN}-kvm-demo"
FILES:${PN}-kvm-demo += "${sysconfdir}/xdg/AGL/kuksa.toml.kvm-demo"
RDEPENDS:${PN}-kvm-demo = "kuksa-certificates-agl-ca"
RPROVIDES:${PN}-kvm-demo = "kuksa-toml"
ALTERNATIVE:${PN}-kvm-demo = "kuksa-toml"
ALTERNATIVE_TARGET_${PN}-kvm-demo = "${sysconfdir}/xdg/AGL/kuksa.toml.kvm-demo"

PACKAGE_BEFORE_PN += "${PN}-kvm-demo-preconfigured"
FILES:${PN}-kvm-demo-preconfigured += "${sysconfdir}/xdg/AGL/kuksa.toml.kvm-demo-preconfigured"
RDEPENDS:${PN}-kvm-demo-preconfigured = "kuksa-certificates-agl-ca"
RPROVIDES:${PN}-kvm-demo-preconfigured = "kuksa-toml"
ALTERNATIVE:${PN}-kvm-demo-preconfigured = "kuksa-toml"
ALTERNATIVE_TARGET_${PN}-kvm-demo-preconfigured = "${sysconfdir}/xdg/AGL/kuksa.toml.kvm-demo-preconfigured"
