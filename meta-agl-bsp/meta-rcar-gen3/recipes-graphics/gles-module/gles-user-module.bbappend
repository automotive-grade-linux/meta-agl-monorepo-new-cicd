require checksum_control.inc

RDEPENDS:${PN}:append = " wayland-wsegl"

do_install:append(){
	 sed -i 's/GROUP="video"/GROUP="display"/g' ${D}${sysconfdir}/udev/rules.d/72-pvr-seat.rules 
}
