FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
	file://nanopct6_output.cfg \
"

WESTON_FRAGMENTS:append:nanopct6 = " nanopct6_output"
