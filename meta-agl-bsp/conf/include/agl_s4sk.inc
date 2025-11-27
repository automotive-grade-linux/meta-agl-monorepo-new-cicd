SOC_FAMILY = "r8a779f0"
BOARD_NAME = "s4sk"

#ws2.0
DTB_SUFFIX = "r8a779f0-s4sk"
WKS_FILE = "singlepart-noloader.wks"
IMAGE_INSTALL:append = " \
    kernel-devicetree \
"

# Mask out base-files and systemd bbappends to avoid changing systemd-tmpfiles
# behavior, see SPEC-4256 for a more involved explanation.
BBMASK += "meta-renesas/meta-rcar-gateway/recipes-core/base-files/ meta-renesas/meta-rcar-gateway/recipes-core/systemd/"

