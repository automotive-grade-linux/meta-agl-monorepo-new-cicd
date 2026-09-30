# Loosely derived from meta-virtualization's xen-raspberrypi4-64.inc

# We have our own kernel configurations for Xen, disable
# meta-virtualization's.
BBMASK += "meta-virtualization/recipes-kernel/linux/"

# Install Xen into boot partition
IMAGE_BOOT_FILES:append = " xen-${MACHINE}"

# We need the GIC enabled for Xen to work
RPI_EXTRA_CONFIG:append  = "\nenable_gic=1"

# Increase boot partition for space for kernel and Xen
BOOT_SPACE = "65536"

PREFERRED_PROVIDER_u-boot-default-script ?= "xen-rpi-u-boot-scr"
