AGL_APPFW_LAYERS = " \
  ${METADIR}/external/meta-security \
  ${METADIR}/external/meta-openembedded/meta-perl \
  ${METADIR}/meta-agl/meta-app-framework \
    "

BBLAYERS =+ "${AGL_APPFW_LAYERS}"

# Indirection to avoid duplicate inclusions of the same folder into BBLAYERS
# the evaluation is in the bblayers.conf.sample in meta-agl/templates/base
AGL_META_PYTHON = "${METADIR}/external/meta-openembedded/meta-python"
AGL_META_NETWORKING = "${METADIR}/external/meta-openembedded/meta-networking"
