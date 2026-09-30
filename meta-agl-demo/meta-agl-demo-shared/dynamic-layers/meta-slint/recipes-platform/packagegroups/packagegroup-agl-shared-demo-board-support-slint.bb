SUMMARY = "AGL Shared Demo Packages for slint - Board Variation Support."
DESCRIPTION = "This package group including Board specific packages for AGL Shared Demo Images with slint."
HOMEPAGE = "https://confluence.automotivelinux.org/display/IC"

LICENSE = "Apache-2.0"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup

PACKAGES = "\
    packagegroup-agl-shared-demo-board-support-slint \
"

# For All.
RDEPENDS:${PN} = " \
"

# For Raspberry Pi boards.
RDEPENDS:${PN}:append:rpi = " \
    libegl-mesa libgles2-mesa \
    mesa-megadriver \
    mesa-vulkan-drivers \
"

# For Rockchip rK3588 boards.
RDEPENDS:${PN}:append:rk3588 = " \
    libegl-mesa libgles2-mesa \
    mesa-megadriver \
    mesa-vulkan-drivers \
    linux-firmware-rtl8822 \
    linux-firmware-rtl-nic \
    linux-firmware-mali-csffw-arch108 \
"

# For Virtio.
RDEPENDS:${PN}:append:virtio-all = " \
    libegl-mesa libgles2-mesa \
    mesa-megadriver \
    mesa-vulkan-drivers \
"

# For R-Car Gen3
RDEPENDS:${PN}:append:rcar-gen3 = " \
"

# For R-Car Gen4
RDEPENDS:${PN}:append:rcar-gen4 = " \
    gles-user-module kernel-module-gles \
"
