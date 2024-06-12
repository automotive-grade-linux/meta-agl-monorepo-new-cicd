SOC_FAMILY = "r8a779f0"
BOARD_NAME = "s4sk"

#ws2.0
DTB_SUFFIX = "r8a779f0-s4sk"
WKS_FILE = "singlepart-noloader.wks"
IMAGE_INSTALL:append = " \
    kernel-devicetree \
"

# For systemd CAN bus configuration
#MACHINE_FEATURES:append = " canfd"
CANBUS_NETWORK_CONFIG = "canbus-can-fd.network"

