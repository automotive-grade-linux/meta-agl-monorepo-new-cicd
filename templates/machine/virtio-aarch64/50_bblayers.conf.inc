BBFILES += " \
    ${METADIR}/meta-agl/meta-agl-bsp/virtualization-layer/recipes*/*/*.bb \
    ${METADIR}/meta-agl/meta-agl-bsp/virtualization-layer/recipes*/*/*.bbappend \
    "

BBMASK += "meta-agl/meta-agl-bsp/virtualization-layer/recipes-extended/xen"
