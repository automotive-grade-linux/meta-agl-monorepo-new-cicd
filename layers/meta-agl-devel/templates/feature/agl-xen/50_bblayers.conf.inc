# DEPENDENCIES
# Needed for meta-virtualization
AGL_META_FILESYSTEMS = "${METADIR}/external/meta-openembedded/meta-filesystems"
AGL_META_NETWORKING = "${METADIR}/external/meta-openembedded/meta-networking"
AGL_META_PYTHON = "${METADIR}/external/meta-openembedded/meta-python"
# Needed for Xen
AGL_META_VIRTUALIZATION = "${METADIR}/external/meta-virtualization"

BBLAYERS =+ " \
	${METADIR}/meta-agl-devel/meta-agl-xen \
	"
