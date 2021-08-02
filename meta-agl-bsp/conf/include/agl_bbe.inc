# This config is for the Sancloud BBE, adapted from the config provider for the
# standard Beaglebone

#AGL_DEFAULT_IMAGE_FSTYPES += "tar.xz"

CORE_IMAGE_EXTRA_INSTALL += "packagegroup-machine-base"

#-------------------------------------------------
## Graphics section ##
#-------------------------------------------------
PACKAGES_GFX = "ti-sgx-ddk-km"
IMAGE_INSTALL:append = " ${PACKAGES_GFX}"


# Enable Gfx Pkgs
MACHINE_FEATURES:append = " sgx"
MULTI_PROVIDER_WHITELIST += "virtual/libgl virtual/egl virtual/libgles1 virtual/libgles2"

DEPENDS:remove = "virtual/libgl"

# Preferred providers
PREFERRED_PROVIDER_virtual/libgles1 = ""
PREFERRED_PROVIDER_virtual/libgles2 = "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/egl      = "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/libgbm   = "ti-sgx-ddk-um"
PREFERRED_PROVIDER_virtual/mesa     = "mesa-gl"

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
