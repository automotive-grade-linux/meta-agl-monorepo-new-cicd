do_install:append() {
	sed -i '/^UMASK/s:^.*$:UMASK 077:' ${D}${sysconfdir}/login.defs
}
