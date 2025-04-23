# Maximum memory allowed to be assigned
GPU_MEM = "256"

# use u-boot always
RPI_USE_U_BOOT = "1"

# Use UART for serial console
ENABLE_UART ?= "1"

# For libomxil
#LICENSE_FLAGS_ACCEPTED = "commercial"

# For linux-firmware-rpidistro
LICENSE_FLAGS_ACCEPTED += "synaptics-killswitch"

IMAGE_INSTALL:append = " kernel-modules create-combined-dtb "

# Psplash causes crash on first boot on RPi
IMAGE_FEATURES:remove = "splash"

#DISPLAY CONFIGURATION
WESTON_DISPLAYS:raspberrypi5 = " hdmi-a-1-90 hdmi-a-2-90"
