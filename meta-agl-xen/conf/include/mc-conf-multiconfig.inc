# Configuration common to all multiconfigs except the default

# TMPDIR override expected for all multiconfigs by agl-xen-image.bbclass
TMPDIR = "${TOPDIR}/tmp-${BB_CURRENT_MC}"

# agl-xen-image.bbclass only needs ext4 images for guests
AGL_DEFAULT_IMAGE_FSTYPES = "ext4"
AGL_EXTRA_IMAGE_FSTYPES = ""
