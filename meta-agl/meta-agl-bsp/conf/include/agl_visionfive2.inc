PERF:forcevariable:visionfive2 = ""

EXTRA_IMAGEDEPENDS:append:visionfive2 = " grub-efi"

# Install compatibility env file for booting with the older
# StarFive U-Boot.
EXTRA_IMAGEDEPENDS:append:visionfive2 = " u-boot-starfive-env"
IMAGE_EFI_BOOT_FILES:append:visionfive2 = " vf2_uEnv.txt"

# Install mtd-utils to facilitate updating the firmware from inside
# Linux.
MACHINE_EXTRA_RRECOMMENDS:append:visionfive2 = " mtd-utils"
