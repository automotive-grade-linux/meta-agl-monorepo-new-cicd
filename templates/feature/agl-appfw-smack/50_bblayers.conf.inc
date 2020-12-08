AGL_APPFW_LAYERS = " \
  ${METADIR}/external/meta-security \
  ${METADIR}/external/meta-openembedded/meta-python \
  ${METADIR}/external/meta-openembedded/meta-networking \
  ${METADIR}/external/meta-openembedded/meta-perl \
  ${METADIR}/meta-agl/meta-app-framework \
    "

BBLAYERS =+ "${AGL_APPFW_LAYERS}"