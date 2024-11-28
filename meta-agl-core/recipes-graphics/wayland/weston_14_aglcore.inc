FILESEXTRAPATHS:append := ":${THISDIR}/weston"

SRC_URI:append = " \
		  file://0001-clients-Handle-missing-pointer_surface-is-there-s-no.patch \
"

WESTON_MAJOR_VERSION = "${@'.'.join(d.getVar('PV').split('.')[0:1])}"

# Split weston-terminal out of the main package to allow installing it
# separately for use with other compositors.  The icons, etc. that end
# up in /usr/share/weston are also split out into a separate package
# since they are required for weston-terminal and some of the programs
# packaged in weston-examples in addition to ivi-shell in the main
# package.

PACKAGE_BEFORE_PN += "${PN}-data ${PN}-terminal ${PN}-pipewire"

FILES:${PN}-pipewire = "${libdir}/libweston-${WESTON_MAJOR_VERSION}/pipewire-backend.so"
FILES:${PN}-data = "${datadir}/weston"

FILES:${PN}-terminal = "${bindir}/weston-terminal"
RDEPENDS:${PN}-terminal += "weston-data liberation-fonts"

RDEPENDS:${PN}-examples += "weston-data"

FILES:${PN}:remove = "weston-terminal"
RDEPENDS:${PN}:remove = "liberation-fonts"
RDEPENDS:${PN} += "weston-data weston-terminal"
