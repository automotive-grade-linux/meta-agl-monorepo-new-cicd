FILESEXTRAPATHS:append := ":${THISDIR}/${PN}"
FILES:${PN} += "/etc/ti-sgx/"

SRC_URI:append = "\
    file://pvr.service \
"

inherit systemd

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "pvr.service"

do_install:append() {
	install -d ${D}${systemd_system_unitdir}
	install -m 0755 ${WORKDIR}/pvr.service ${D}${systemd_system_unitdir}
	install -d ${D}/etc/ti-sgx
	install -m 0755 ${D}/etc/init.d/rc.pvr ${D}/etc/ti-sgx
}
