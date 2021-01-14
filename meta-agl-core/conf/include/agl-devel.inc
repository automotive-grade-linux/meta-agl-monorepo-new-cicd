IMAGE_FEATURES =+ "debug-tweaks tools-debug tools-profile"

IMAGE_INSTALL_append = " \
    packagegroup-agl-core-devel \
"

# Install libdrm-test, including modetest, to images with GUI
IMAGE_INSTALL_append = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', ' libdrm-tests', '', d)}"

IMAGE_FEATURES_append = " agl-devel-wgt"

# disable install of debug files in SDK
# initial value: SDKIMAGE_FEATURES="dev-pkgs dbg-pkgs staticdev-pkgs"
SDKIMAGE_FEATURES="dev-pkgs"

# allows insertion of code or items specific to developement
OVERRIDES .= ":agl-devel"
DISTRO_FEATURES_append = " agl-devel"
