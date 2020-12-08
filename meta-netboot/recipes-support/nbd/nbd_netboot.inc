do_install_append() {
	mv ${D}/${sbindir}/nbd-client ${D}/${sbindir}/nbd3-client
}

FILES_${PN}-client_append = " ${sbindir}/nbd3-client"
