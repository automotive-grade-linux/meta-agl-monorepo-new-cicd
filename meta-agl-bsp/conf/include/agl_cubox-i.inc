require agl_imx6-common.inc

# Include devicetrees for SOM 1.5 and Hummingboard 2 hardware
KERNEL_DEVICETREE:append = " \
	imx6dl-hummingboard-som-v15.dtb \
	imx6q-hummingboard-som-v15.dtb \
	imx6dl-hummingboard2.dtb \
	imx6q-hummingboard2.dtb \
	imx6dl-hummingboard2-som-v15.dtb \
	imx6q-hummingboard2-som-v15.dtb \
"
