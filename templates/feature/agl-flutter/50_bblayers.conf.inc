AGL_META_CLANG = " ${METADIR}/external/meta-clang "
# Required for libcamera
AGL_META_MULTIMEDIA = "${METADIR}/external/meta-openembedded/meta-multimedia"
AGL_META_PYTHON = "${METADIR}/external/meta-openembedded/meta-python"

BBLAYERS =+ " \
   ${METADIR}/meta-agl/meta-agl-flutter \
   ${METADIR}/external/meta-flutter \
   ${METADIR}/external/meta-flutter/meta-flutter-apps \
"
