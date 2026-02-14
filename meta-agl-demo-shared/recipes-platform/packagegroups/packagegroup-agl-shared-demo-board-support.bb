SUMMARY = "AGL Shared Demo Packages - Board Variation Support."
DESCRIPTION = "This package group including Board specific packages for AGL Shared Demo Images."
HOMEPAGE = "https://confluence.automotivelinux.org/display/IC"

LICENSE = "Apache-2.0"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup

PACKAGES = "\
    packagegroup-agl-shared-demo-board-support \
"

# For All.
RDEPENDS:${PN} = " \
"

# For Raspberry Pi boards.
RDEPENDS:${PN}:append:rpi = " \
    mesa-megadriver \
"

# For Virtio.
RDEPENDS:${PN}:append:virtio-all = " \
    mesa-megadriver \
"

# For R-Car Gen3
RDEPENDS:${PN}:append:rcar-gen3 = " \
"

# For R-Car Gen4
RDEPENDS:${PN}:append:rcar-gen4 = " \
"
