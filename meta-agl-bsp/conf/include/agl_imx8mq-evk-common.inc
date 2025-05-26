require agl_imx-common.inc

# Add helper to drive setting up HCI UART device
# NOTE: Disabled 2023/05 due to kernel oops, further investigation
#       required, see SPEC-4807 in JIRA.
#MACHINE_EXTRA_RRECOMMENDS:append = " hci-uart-helper"

AGL_DEFAULT_INITRAMFS_FSTYPES := ""

# Disable pipewire bbappend as it disables the gstreamer plugin that we
# require in a not easily reversed way, and the other changes are not
# particularly useful, anyways.
BBMASK += "meta-freescale/dynamic-layers/multimedia-layer/recipes-multimedia/pipewire/pipewire_%.bbappend"
