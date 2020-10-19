FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append = "\
    	file://0001-waltham-Fix-compile-build-error.patch \
	file://0002-commandxml-Add-support-wthp_app_id-interface.patch \
	file://0001-Use-python3-instead-of-2.patch \
	"
