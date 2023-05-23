require agl_imx8mq-evk-common.inc

# NOTE:
# Currently using the upstream default of IMX_DEFAULT_BSP = "mainline",
# since we want to use the etnaviv driver and upstream Mesa + weston
# stack.  More tweaks are required to accomplish that if the BSP is set
# to "nxp".

# Force use of the community supported hybrid NXP/LTS kernel instead of
# the default linux-fslc.  This is required to get the drivers required
# for HDMI output.
IMX_DEFAULT_KERNEL:imx8mq-evk = "linux-fslc-imx"

# Tweaks to allow using linux-fslc-imx on its own without the whole
# NXP BSP.  Just the DISTROOVERRIDES change on its own is not enough,
# as COMPATIBLE_MACHINE is evaluated early enough that the recipe is
# not seen as compatible without an explicit override.
COMPATIBLE_MACHINE:pn-linux-fslc-imx = "(imx8mq-evk)"
DISTROOVERRIDES:pn-linux-fslc-imx:append = "mx8-nxp-bsp"

# Add our own over-ride for use in a few recipes where there is
# conflicting Vivante vs etnaviv configuration.
MACHINEOVERRIDES .= ":etnaviv"
