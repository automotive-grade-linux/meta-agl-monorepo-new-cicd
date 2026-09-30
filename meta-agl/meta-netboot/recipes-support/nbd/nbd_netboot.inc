do_install:append() {
	mv ${D}/${sbindir}/nbd-client ${D}/${sbindir}/nbd3-client
}

FILES:${PN}-client:append = " ${sbindir}/nbd3-client"
