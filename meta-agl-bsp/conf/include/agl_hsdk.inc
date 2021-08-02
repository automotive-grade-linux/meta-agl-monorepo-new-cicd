KERNEL_IMAGETYPE = "uImage"
WKS_FILE = "sdimage-hsdk.wks"
IMAGE_BOOT_FILES = "uImage uboot.env"
IMAGE_INSTALL:append = " kernel-modules"

# remove incompatible packages from packagegroups
RDEPENDS:packagegroup-agl-core-devel:remove:hsdk = "valgrind gcc-sanitizers"
RDEPENDS:packagegroup-core-tools-profile:remove:hsdk = "systemtap"
RDEPENDS:packagegroup-core-tools-debug:remove:hsdk = "gdbserver"
