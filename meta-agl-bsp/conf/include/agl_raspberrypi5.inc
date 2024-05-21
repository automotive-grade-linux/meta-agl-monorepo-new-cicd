# Maximum memory allowed to be assigned
GPU_MEM = "256"

# Add CMA to the kernel arguments for SOTA
OSTREE_KERNEL_ARGS:sota:append = " cma=256M"

# use u-boot always
RPI_USE_U_BOOT = "1"

# Re-enable U-Boot for raspberrypi5 here until upstream is fixed.
# Note that this assumes the use of scarthgap/u-boot meta-lts-mixins
# branch.
UBOOT_MACHINE = "rpi_arm64_config"
COMPATIBLE_MACHINE:pn-u-boot:forcevariable = ""
KERNEL_IMAGETYPE_UBOOT ?= "Image"
KERNEL_BOOTCMD ?= "booti"

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
