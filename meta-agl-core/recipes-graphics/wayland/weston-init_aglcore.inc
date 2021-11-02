SYSTEMD_AUTO_ENABLE = "enable"

do_install:append() {
    # Remove upstream weston.ini to avoid conflict with weston-ini-conf package
    rm -f ${D}${sysconfdir}/xdg/weston/weston.ini
}

RDEPENDS:${PN} += "weston-ini"

# Temporary effective backport of upstream commit ccdaab97
# This gets us on the same page as upstream with respect to running
# weston as non-root, and should make migrating to 3.5/Kirkstone
# easier.

FILESEXTRAPATHS:prepend := "${THISDIR}/weston-init:"

SRC_URI += " \
           file://weston.service \
           file://weston.socket \
           file://weston-autologin \
"

do_install:append() {
	rm -f ${D}${sysconfdir}/udev/rules.d/71-weston-drm.rules
	rm -f ${D}${systemd_system_unitdir}/weston@.service
	rm -f ${D}${systemd_system_unitdir}/weston@.socket

	install -D -p -m0644 ${WORKDIR}/weston.service ${D}${systemd_system_unitdir}/weston.service
	install -D -p -m0644 ${WORKDIR}/weston.socket ${D}${systemd_system_unitdir}/weston.socket
        if [ "${@bb.utils.filter('DISTRO_FEATURES', 'pam', d)}" ]; then
               install -D -p -m0644 ${WORKDIR}/weston-autologin ${D}${sysconfdir}/pam.d/weston-autologin
        fi

	sed -i -e s:/etc:${sysconfdir}:g \
		-e s:/usr/bin:${bindir}:g \
		-e s:/var:${localstatedir}:g \
		${D}${systemd_system_unitdir}/weston.service

	install -dm 755 -o weston -g weston ${D}/home/weston
}

inherit useradd

USERADD_PACKAGES = "${PN}"

FILES_${PN} += "\
    ${systemd_system_unitdir}/weston.service \
    ${systemd_system_unitdir}/weston.socket \
    /home/weston \
"

SYSTEMD_SERVICE_${PN} = "weston.service weston.socket"
USERADD_PARAM_${PN} = "--home /home/weston --shell /bin/sh --user-group -G video,input weston"
GROUPADD_PARAM_${PN} = "-r wayland"
