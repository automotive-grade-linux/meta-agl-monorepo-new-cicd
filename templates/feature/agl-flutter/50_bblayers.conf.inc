AGL_META_CLANG = " ${METADIR}/external/meta-clang "

BBLAYERS =+ " \
   ${METADIR}/meta-agl/meta-agl-flutter \
   ${METADIR}/external/meta-flutter \
   ${METADIR}/external/meta-flutter/meta-flutter-apps \
"
