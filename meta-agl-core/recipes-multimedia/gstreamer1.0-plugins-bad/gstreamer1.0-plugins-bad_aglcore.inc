FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

AGL_PATCHES = "file://0001-Added-appid-and-title-support.patch"

# Above patch fails to apply against NXP's forked version, disable
# for now
AGL_PATCHES:use-nxp-bsp = ""

SRC_URI:append = " ${AGL_PATCHES}"
