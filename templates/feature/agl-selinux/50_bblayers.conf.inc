AGL_META_PYTHON = "${METADIR}/external/meta-openembedded/meta-python"

# Needed to satisfy uutils-coreutils DEPENDS when using SELinux
AGL_META_CLANG = "${METADIR}/external/meta-clang"

BBLAYERS =+ " \
	${METADIR}/external/meta-selinux \
	"
