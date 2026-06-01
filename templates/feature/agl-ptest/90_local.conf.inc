
# Enabling ptest in image ...

OVERRIDES:append = ":agl-ptest"
DISTRO_FEATURES:append = " ptest"
EXTRA_IMAGE_FEATURES:append = " ptest-pkgs"
IMAGE_INSTALL:append = " lua-ptest"

# / ptest
