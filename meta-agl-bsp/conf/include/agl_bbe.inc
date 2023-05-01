# This config is for the Sancloud BBE, adapted from the config provider for the
# standard Beaglebone

#AGL_DEFAULT_IMAGE_FSTYPES += "tar.xz"

CORE_IMAGE_EXTRA_INSTALL += "packagegroup-machine-base"

#-------------------------------------------------
## Multimedia section ##
#-------------------------------------------------

# DEFAULTTUNE for cortex-a8
DISTRO_FEATURES:append = " agl-medium-arm-compiler"

SECURITY_CFLAGS:pn-mozjs = ""
SECURITY_LDFLAGS:pn-mozjs = ""
SECURITY_CFLAGS:pn-gpm = ""
SECURITY_LDFLAGS:pn-gpm = ""
SECURITY_CFLAGS:pn-libatasmart = ""
SECURITY_LDFLAGS:pn-libatasmart = ""
