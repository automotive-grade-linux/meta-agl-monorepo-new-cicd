# Maximum memory allowed to be assigned
GPU_MEM = "256"

# use u-boot always
RPI_USE_U_BOOT = "1"

# Use UART for serial console
ENABLE_UART ?= "1"

# add pi500
# to enable the pi500, add to your conf/local.conf: 
#
OVERRIDES:append = ":pi500"
#
RPI_USE_U_BOOT:pi500 = "0"
ENABLE_UART:pi500 = "0"
RPI_KERNEL_DEVICETREE:append:pi500 = " broadcom/bcm2712-rpi-500.dtb "
SERIAL_CONSOLES:pi500 = ""
BABELTRACE:pi500 = ""
BABELTRACE2:pi500 = ""
LTTNGUST:pi500 = ""
LTTNGMODULES:pi500 = ""
LTTNGTOOLS:pi500 = ""

# For libomxil
#LICENSE_FLAGS_ACCEPTED = "commercial"

# For linux-firmware-rpidistro
LICENSE_FLAGS_ACCEPTED += "synaptics-killswitch"

IMAGE_INSTALL:append = " kernel-modules create-combined-dtb "

# Psplash causes crash on first boot on RPi
IMAGE_FEATURES:remove = "splash"

#DISPLAY CONFIGURATION
WESTON_DISPLAYS:raspberrypi500 = " hdmi-a-1-90 hdmi-a-2-90"
