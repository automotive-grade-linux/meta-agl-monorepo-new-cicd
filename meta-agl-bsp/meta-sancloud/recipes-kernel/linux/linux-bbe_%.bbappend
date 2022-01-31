require recipes-kernel/linux/linux-agl.inc

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

AGL_KCONFIG_FRAGMENTS += "cma-256.cfg"

SRC_URI += "file://0001-Revert-block-nbd-add-sanity-check-for-first_minor.patch"
