IMAGE_LINGUAS = " "

inherit core-image

python __anonymous () {
    d.setVar("IMAGE_FSTYPES", d.getVar("AGL_DEFAULT_IMAGE_FSTYPES"))
    d.appendVar("IMAGE_FSTYPES", " " + d.getVar("AGL_EXTRA_IMAGE_FSTYPES"))
}

IMAGE_INSTALL = "${CORE_IMAGE_EXTRA_INSTALL}"

IMAGE_ROOTFS_SIZE ?= "8192"
