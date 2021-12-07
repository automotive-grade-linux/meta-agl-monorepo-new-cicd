SUMMARY = "AGL user session"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "\
    file://agl-session@.service \
    file://agl-session.target \
"

inherit systemd allarch useradd

USERADD_PACKAGES = "${PN}"
USERADDEXTENSION = "useradd-staticids"
GROUPADD_PARAM:${PN} = "\
	-g 1001 agl-driver ; \
"
USERADD_PARAM:${PN} = "\
  -g 1001 -u 1001 -o -d /home/agl-driver -m -K PASS_MAX_DAYS=-1 agl-driver ; \
"

SYSTEMD_PACKAGES = "${PN}"
# Instantiate session for the 'agl-driver' user, so we don't have to hardcode
# the user name/ID in the service file itself
SYSTEMD_SERVICE:${PN} = "agl-session@agl-driver.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

do_install() {
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${WORKDIR}/agl-session@.service ${D}${systemd_system_unitdir}

    install -d ${D}${systemd_user_unitdir}
    install -m 0644 ${WORKDIR}/agl-session.target ${D}${systemd_user_unitdir}
}

FILES:${PN} += "${systemd_system_unitdir} ${systemd_user_unitdir}"
