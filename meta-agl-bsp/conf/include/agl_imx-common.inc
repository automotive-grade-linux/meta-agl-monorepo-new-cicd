# Disable meta-freescale package architecure mangling, as it causes
# issues with AGL's explicit setting of DEFAULTTUNE.
INHERIT_remove = "fsl-dynamic-packagearch"
MACHINE_SOCARCH = "${TUNE_PKGARCH}"

# Disable the meta-freescale weston-init bbappend as it conflicts
# with AGL's.
BBMASK += "meta-freescale/recipes-graphics/wayland/weston-init"
