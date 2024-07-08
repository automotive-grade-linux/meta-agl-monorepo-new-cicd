# PipeWire recipe comes from meta-multimedia
AGL_META_MULTIMEDIA = "${METADIR}/external/meta-openembedded/meta-multimedia"
AGL_META_PYTHON = "${METADIR}/external/meta-openembedded/meta-python"

BBLAYERS =+ " \
    ${METADIR}/meta-agl/meta-pipewire \
"
