PREFERRED_PROVIDER_virtual/egl ?= "mesa"
PREFERRED_PROVIDER_virtual/libgl ?= "mesa"
PREFERRED_PROVIDER_virtual/libgles1 ?= "mesa"
PREFERRED_PROVIDER_virtual/libgles2 ?= "mesa"

MACHINE_FEATURES = "alsa usbgadget screen vfat"

MACHINEOVERRIDES =. "virtio-all:"

IMAGE_FSTYPES += "tar.bz2 ext4"

# Don't include kernels in standard images
RDEPENDS_${KERNEL_PACKAGE_NAME}-base = ""

# Use a common kernel recipe for all VirtIO machines
PREFERRED_PROVIDER_virtual/kernel ??= "linux-yocto"

# Use newer kernel
PREFERRED_VERSION_linux-yocto = "5.10.%"

# Clear since it is set to 'sllin' in packagegroup-agl-demo.bb, and sllin is not
# compatible with kernel 5.10 at the moment.
LIN_DRIVERS = ""

# Clear since it is set to 'lttng-modules' in packagegroup-core-tools-profile.bb,
# and lttng-modules are not compatible with 5.10 kernel used for virtio.
LTTNGMODULES_virtio-all = ""

EXTRA_IMAGEDEPENDS += "qemu-native qemu-helper-native"

IMAGE_CLASSES += "qemuboot"

# most driver fails to compile
MOST_DRIVERS ?= ""
