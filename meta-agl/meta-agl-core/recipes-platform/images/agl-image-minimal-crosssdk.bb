require agl-image-minimal.bb

SUMMARY = "Cross SDK of minimal AGL Distribution for core profile"
DESCRIPTION = "SDK image for AGL core distribution. \
It includes the full toolchain, plus developement headers and libraries \
to form a standalone cross SDK."
LICENSE = "MIT"

inherit agl-crosssdk
