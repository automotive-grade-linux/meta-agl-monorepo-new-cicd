#Keep this as a reminder
#MACHINEOVERRIDES .= ":kingfisher"
#PACKAGE_EXTRA_ARCHS:append = " kingfisher"

# Add a feature as a low-impact way to detect Kingfisher support in
# recipes.
AGL_FEATURES:append:rcar-gen3 = " kingfisher"

PREFERRED_RPROVIDER_virtual/gpsd-conf:rcar-gen3 ?= "gpsd-kingfisher-conf"

IMAGE_NAME:rcar-gen3 = "${IMAGE_BASENAME}-${MACHINE}-kf${IMAGE_VERSION_SUFFIX}"
IMAGE_LINK_NAME:rcar-gen3 = "${IMAGE_BASENAME}-${MACHINE}-kf"
TOOLCHAIN_OUTPUTNAME:rcar-gen3 = "${SDK_NAME}-kf-toolchain-${SDK_VERSION}"

# Radio packages
IMAGE_INSTALL:append:rcar-gen3 = " \
    si-tools \
    si-init \
    linux-firmware-wl18xx \
    ti-bt \
    ti-bt-firmware \
"
