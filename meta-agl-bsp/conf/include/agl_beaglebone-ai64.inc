# beaglebone-ai64 has a k3r5 BBMULTICONFIG to build its bootloader
# and firmware, need to use an override to set DEFAULTTUNE back to
# what that machine configuration needs.
DEFAULTTUNE:k3r5 = "armv7athf"

TI_COMMON_DEPLOY := "${TMPDIR}/deploy"
