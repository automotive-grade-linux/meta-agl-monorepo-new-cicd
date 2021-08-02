# j7-evm has a k3r5 BBMULTICONFIG to build its bootloader firmware,
# need to use an override to set DEFAULTTUNE back to what that
# machine configuration needs.
DEFAULTTUNE_k3r5 = "armv7athf"

# Force using a separate TMPDIR for the multiconfig to avoid
# issues (e.g. using the rm_work class currently fails with the
# single shared TMPDIR).
TMPDIR_k3r5 = "${TOPDIR}/tmp-${BB_CURRENT_MC}"
# But we then also need to redefine DEPLOY_DIR for it to match
# the existing expectations (pointing into the default config)
DEPLOY_DIR_k3r5 = "${TOPDIR}/tmp/deploy"

# Workaround for recipe assumption of poky location
LIC_FILES_CHKSUM:pn-ti-rtos-firmware = "file://${THISDIR}/../../licenses/TI-TFL;md5=a1b59cb7ba626b9dbbcbf00f3fbc438a"
