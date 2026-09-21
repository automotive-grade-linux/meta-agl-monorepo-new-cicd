INHERIT += "netboot"

IMAGE_INSTALL:append:netboot = " \
    curl \
    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'resolv-conf-relabel policycoreutils-loadpolicy', '', d)} \
"

# netboot initrd IMAGE_FSTYPE collides with fitimage defaults
# Could not find a valid initramfs type for initramfs-netboot-image-XYZ, 
# the supported types are: cpio.lz4 cpio.lzo cpio.lzma cpio.xz cpio.zst cpio.gz ext2.gz cpio
# turn off fitimage for the netboot
KERNEL_DEPLOY_DEPEND:forcevariable:forcevariable:netboot = ""
MACHINE_ESSENTIAL_EXTRA_RDEPENDS:forcevariable:netboot = ""
MACHINE_ESSENTIAL_EXTRA_RDEPENDS_KERNEL:forcevariable:netboot = ""
KERNEL_CLASSES:forcevariable:netboot = ""


