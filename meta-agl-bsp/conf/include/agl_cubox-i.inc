require agl_imx6-common.inc

# Temporary fix for upstream breakage
# Note that the :cubox-i override is needed because we're
# overriding a definition done in the machine .conf.
KERNEL_DEVICETREE:cubox-i = " \
	nxp/imx/imx6dl-cubox-i.dtb \
	nxp/imx/imx6q-cubox-i.dtb \
	nxp/imx/imx6dl-hummingboard.dtb \
	nxp/imx/imx6q-hummingboard.dtb \
"

# Include devicetrees for SOM 1.5 and Hummingboard 2 hardware
KERNEL_DEVICETREE:append = " \
	nxp/imx/imx6dl-hummingboard-som-v15.dtb \
	nxp/imx/imx6q-hummingboard-som-v15.dtb \
	nxp/imx/imx6dl-hummingboard2.dtb \
	nxp/imx/imx6q-hummingboard2.dtb \
	nxp/imx/imx6dl-hummingboard2-som-v15.dtb \
	nxp/imx/imx6q-hummingboard2-som-v15.dtb \
"
