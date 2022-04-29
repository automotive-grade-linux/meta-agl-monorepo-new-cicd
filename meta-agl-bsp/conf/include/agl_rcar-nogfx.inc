DISTRO_FEATURES:append = " sota"

OSTREE_KERNEL = "Image"
IMAGE_BOOT_FILES:sota = "renesas-ota-bootfiles/* ${DTB_SUFFIX}.dtb"
OSTREE_BOOTLOADER ?= "u-boot"

WKS_FILE = "singlepart-noloader.wks"

RENESAS_DATADIR = "/usr"

IMAGE_INSTALL:append:rcar-gen3 = " \
    kernel-devicetree \
"

#
# Workaround to avoid pseudo aborts triggered by the kernel module
# recipes writing to KERNELSRC in do_install, errors are of the form
# (from pseudo.log):
#
# path mismatch [27 links]: ino 19366408 db 'NAMELESS FILE' req 'build/tmp/work-shared/h3ulcb/kernel-source/include'.
#
# See the commit messages in poky commits 4e7c211 and 3b7f2c6 and the
# wiki page at:
#
# https://wiki.yoctoproject.org/wiki/Pseudo_Abort
#
# for details on the pseudo change.  These can potentially be moved
# to the recipes in the BSP, or removed if the recipes are changed to
# shift the module headers and symbols to their dev packages.
#
PSEUDO_IGNORE_PATHS:append:pn-kernel-module-avb-mch = ",${KERNELSRC}"
PSEUDO_IGNORE_PATHS:append:pn-kernel-module-avb-mse = ",${KERNELSRC}"
PSEUDO_IGNORE_PATHS:append:pn-kernel-module-avb-streaming = ",${KERNELSRC}"
PSEUDO_IGNORE_PATHS:append:pn-kernel-module-scu-src = ",${KERNELSRC}"
PSEUDO_IGNORE_PATHS:append:pn-kernel-module-ssp = ",${KERNELSRC}"
PSEUDO_IGNORE_PATHS:append:pn-kernel-module-tddmac = ",${KERNELSRC}"
PSEUDO_IGNORE_PATHS:append:pn-kernel-module-tsif = ",${KERNELSRC}"
PSEUDO_IGNORE_PATHS:append:pn-kernel-module-mmngr = ",${KERNELSRC}"
PSEUDO_IGNORE_PATHS:append:pn-kernel-module-mmngrbuf = ",${KERNELSRC}"
PSEUDO_IGNORE_PATHS:append:pn-kernel-module-qos = ",${KERNELSRC}"
PSEUDO_IGNORE_PATHS:append:pn-kernel-module-vsp2driver = ",${KERNELSRC}"
PSEUDO_IGNORE_PATHS:append:pn-kernel-module-vspmif = ",${KERNELSRC}"
PSEUDO_IGNORE_PATHS:append:pn-kernel-module-vspm = ",${KERNELSRC}"

# Mask out base-files and systemd bbappends to avoid changing systemd-tmpfiles
# behavior, see SPEC-4256 for a more involved explanation.
BBMASK += "meta-rcar-gen3/recipes-core/base-files/ meta-rcar-gen3/recipes-core/systemd/"

# Mask out cogl bbappend to avoid meta-gnome dependency
BBMASK += "meta-rcar-gen3/recipes-graphics/cogl/cogl-1.0_1.%.bbappend"
