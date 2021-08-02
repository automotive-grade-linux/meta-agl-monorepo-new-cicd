IMAGE_FEATURES:append = " dev-pkgs"
IMAGE_INSTALL:append = " kernel-dev kernel-devsrc"






# FIXME:
# Always include the test widgets
#IMAGE_FEATURES:append = " agl-test-wgt"
#
# Required dependencies for app and test builds
#TOOLCHAIN_HOST_TASK += " \
#    nativesdk-lua \
#    "
#
# Required dependencies for app and test builds
#TOOLCHAIN_TARGET_TASK += " \
#    lua-dev \
#    lua-staticdev \
#    libafb-helpers-staticdev \
#    libappcontroller-staticdev \
#    "
