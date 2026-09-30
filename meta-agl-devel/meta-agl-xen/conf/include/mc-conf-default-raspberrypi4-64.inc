# Loosely derived from meta-virtualization's xen-raspberrypi4-64.inc

# First explicitly disable that .inc file to avoid having to jump
# through hoops to undo some of its changes.  There are some other
# things in the layer that the "xen" DISTRO_FEATURE enables that
# we do want, so this approach is required.
XEN_RPI4_64_CONFIG_PATH = ""

# Install Xen into boot partition
IMAGE_BOOT_FILES:append = " xen-${MACHINE}"

# We need the GIC enabled for Xen to work
RPI_EXTRA_CONFIG:append  = "\nenable_gic=1"

# Increase boot partition for space for kernel and Xen
BOOT_SPACE = "65536"

PREFERRED_PROVIDER_u-boot-default-script ?= "xen-rpi-u-boot-scr"
