FILESEXTRAPATHS_prepend_rcar-gen3 := "${THISDIR}/${PN}:"

SRC_URI_append_rcar-gen3 = " \
	file://kingfisher_output.cfg \
	file://ebisu_output.cfg \
	file://salvator-x_output.cfg \
"

WESTON_FRAGMENTS_append_ulcb = " kingfisher_output"
WESTON_FRAGMENTS_append_ebisu = " ebisu_output"
WESTON_FRAGMENTS_append_salvator-x = " salvator-x_output"

do_configure_append_rcar-gen3() {
    echo repaint-window=34 >> ${WORKDIR}/core.cfg
}
