
# Enabling ptest in image ...

OVERRIDES .= ":agl-ptest"
DISTRO_FEATURES:append = " ptest"
EXTRA_IMAGE_FEATURES:append = " ptest-pkgs"
IMAGE_INSTALL:append = " lua-ptest xmlsec1-ptest libxml2-ptest"

# / ptest
