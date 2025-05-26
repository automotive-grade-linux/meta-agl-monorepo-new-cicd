require agl_imx8mq-evk-common.inc

# NOTE:
# Using the upstream default of IMX_DEFAULT_BSP = "mainline" seems to
# still have issues with respect to getting the HDMI port working, even
# if using the linux-imx or linux-fslc-imx kernels that have the required
# MHDP driver.  At present (i.e. with scarthgap) getting just the kernel
# and u-boot configurations tweaked to use the NXP versions has proven
# more complicated than using the NXP BSP option and disabling Vivante
# use.
IMX_DEFAULT_BSP = "nxp"

# Knock out Vivante overrides
# Clearing the variable completely avoids the need to override a lot
# of the PREFERRED_VERSION definitions (see below), but causes more
# fallout with the kernel and bootloader recipes that would require
# more involved workarounds.
MACHINEOVERRIDES_EXTENDER:mx8mq:forcevariable = "imx-generic-bsp:imx-nxp-bsp:mx8-generic-bsp:mx8-nxp-bsp:mx8m-generic-bsp:mx8m-nxp-bsp:mx8mq-generic-bsp:mx8mq-nxp-bsp"

# Add our own over-ride for use in a few recipes where there is
# conflicting Vivante vs etnaviv configuration.
MACHINEOVERRIDES .= ":etnaviv"

# Reset graphics-related recipes to mainline versions

PREFERRED_VERSION_weston:mx8mq-generic-bsp = "13.0.1"
PREFERRED_VERSION_wayland-protocols:mx8mq-generic-bsp = "1.33"
PREFERRED_VERSION_xwayland:mx8mq-generic-bsp = "23.2.5"
PREFERRED_VERSION_libdrm:mx8mq-generic-bsp = "2.4.120"
PREFERRED_VERSION_ffmpeg:mx8mq-generic-bsp = "6.6.%"

PREFERRED_VERSION_gstreamer1.0:mx8mq-generic-bsp = "1.22.%"
PREFERRED_VERSION_gstreamer1.0-plugins-base:mx8mq-generic-bsp = "1.22.%"
PREFERRED_VERSION_gstreamer1.0-plugins-good:mx8mq-generic-bsp = "1.22.%"
PREFERRED_VERSION_gstreamer1.0-plugins-bad:mx8mq-generic-bsp = "1.22.%"
PREFERRED_VERSION_gstreamer1.0-plugins-ugly:mx8mq-generic-bsp = "1.22.%"
PREFERRED_VERSION_gstreamer1.0-python:mx8mq-generic-bsp = "1.22.%"
PREFERRED_VERSION_gstreamer1.0-rtsp-server:mx8mq-generic-bsp = "1.22.%"
PREFERRED_VERSION_gstreamer1.0-vaapi:mx8mq-generic-bsp = "1.22.%"
PREFERRED_VERSION_gst-devtools:mx8mq-generic-bsp = "1.22.%"

# Disable to avoid warnings about missing bbappend for the preferred
# version in some scenarios.
BBMASK += "meta-freescale/recipes-multimedia/gstreamer/gstreamer1.0-plugins-base_1.24.%.bbappend"
