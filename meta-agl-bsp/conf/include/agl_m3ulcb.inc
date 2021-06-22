require conf/include/agl_rcar.inc
SOC_FAMILY = "r8a7796"
BOARD_NAME = "m3ulcb"

DTB_SUFFIX = "r8a77960-ulcb"

DISTRO_FEATURES:append = "${@bb.utils.contains('AGL_XEN_WANTED', '1', ' xen', '', d)}"
IMAGE_INSTALL:append = "${@bb.utils.contains('AGL_XEN_WANTED', '1', ' xen-base xen-mkimage', '', d)}"
