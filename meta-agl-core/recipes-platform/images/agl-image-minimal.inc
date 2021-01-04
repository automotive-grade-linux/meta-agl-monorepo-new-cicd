IMAGE_LINGUAS = " "

# Reset IMAGE_FSTYPES to our specific desired value to avoid issues
# with _append or _remove declarations against IMAGE_FSTYPES.
# Note that this snippet needs to be before (core-)image bbclass
# inclusion to take effect.
python __anonymous () {
    d.setVar("IMAGE_FSTYPES", d.getVar("AGL_DEFAULT_IMAGE_FSTYPES"))
    d.appendVar("IMAGE_FSTYPES", " " + d.getVar("AGL_EXTRA_IMAGE_FSTYPES"))
}

inherit core-image

IMAGE_INSTALL = "${CORE_IMAGE_EXTRA_INSTALL}"

IMAGE_ROOTFS_SIZE ?= "8192"
