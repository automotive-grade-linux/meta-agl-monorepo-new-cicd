require agl_imx-common.inc

# Add helper to drive setting up HCI UART device
# NOTE: Disabled 2023/05 due to kernel oops, further investigation
#       required, see SPEC-4807 in JIRA.
#MACHINE_EXTRA_RRECOMMENDS:append = " hci-uart-helper"

AGL_DEFAULT_INITRAMFS_FSTYPES := ""
