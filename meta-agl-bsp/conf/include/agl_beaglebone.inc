# This config is for the beaglebone (inspired from the vayu config).

# DEFAULTTUNE for cortex-a8
DISTRO_FEATURES:append = " agl-medium-arm-compiler"

WKS_FILE = "sdimage-bootpart-uuid.wks"

#-------------------------------------------------
## Graphics section ##
#-------------------------------------------------
PACKAGES_GFX = "ti-sgx-ddk-km"
IMAGE_INSTALL:append = " ${PACKAGES_GFX}"

#  Enable Gfx Pkgs
MACHINE_FEATURES:append = " gpu"
BB_MULTI_PROVIDER_ALLOWED += "virtual/libgl virtual/egl virtual/libgles1 virtual/libgles2 virtual/libgles3"

# NOTE: SGX GPU driver use disabled until TI provides updated drivers
#       that Weston 10.0.0 can be built against.

# Preferred providers
#PREFERRED_PROVIDER_virtual/libgles1 = ""
#PREFERRED_PROVIDER_virtual/libgles2 = "ti-sgx-ddk-um"
#PREFERRED_PROVIDER_virtual/libgles3 = "ti-sgx-ddk-um"
#PREFERRED_PROVIDER_virtual/egl      = "ti-sgx-ddk-um"
#PREFERRED_PROVIDER_virtual/libgbm   = "ti-sgx-ddk-um"
#PREFERRED_PROVIDER_virtual/mesa     = "mesa-gl"
