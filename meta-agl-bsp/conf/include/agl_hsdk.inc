KERNEL_IMAGETYPE = "uImage"
WKS_FILE = "sdimage-hsdk.wks"
IMAGE_BOOT_FILES = "uImage uboot.env"
IMAGE_INSTALL_append = " kernel-modules"

# remove incompatible packages from packagegroups
RDEPENDS_packagegroup-agl-core-devel_remove_hsdk = "valgrind gcc-sanitizers"
RDEPENDS_packagegroup-core-tools-profile_remove_hsdk = "systemtap"
RDEPENDS_packagegroup-core-tools-debug_remove_hsdk = "gdbserver"
