AGL_META_PYTHON = "${METADIR}/external/meta-openembedded/meta-python"
AGL_META_MULTIMEDIA = "${METADIR}/external/meta-openembedded/meta-multimedia"
AGL_META_NETWORKING = "${METADIR}/external/meta-openembedded/meta-networking"
AGL_META_VIRTUALIZATION = "${METADIR}/external/meta-virtualization"

BBLAYERS =+ "\
    ${METADIR}/bsp/meta-aws \
    "
