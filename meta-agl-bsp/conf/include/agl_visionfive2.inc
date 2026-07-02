PERF:forcevariable:visionfive2 = ""

EXTRA_IMAGEDEPENDS:append:visionfive2 = " grub-efi"

# Install mtd-utils to facilitate updating the firmware from inside
# Linux.
MACHINE_EXTRA_RRECOMMENDS:append:visionfive2 = " mtd-utils"
