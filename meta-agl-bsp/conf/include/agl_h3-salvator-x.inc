require conf/include/agl_rcar.inc
SOC_FAMILY = "r8a7795"
BOARD_NAME = "h3-salvator-x"

DTB_SUFFIX = "r8a77951-salvator-xs"

IMAGE_INSTALL:append:rcar-gen3 = " \
    kernel-module-vspm-if \
"
