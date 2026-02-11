MACHINE_EXTRA_RRECOMMENDS:append:rcar-gen4 = " \
    alsa-state \
    ${@bb.utils.contains('AGL_FEATURES', 'agl-devel', 'packagegroup-renesas-bsp-tools', '', d)} \
"

# Enable Gfx Pkgs
MACHINE_FEATURES:append:rcar-gen4 = " gsx"

# Enable Wayland/Weston
DISTRO_FEATURES_NATIVESDK:append:rcar-gen4 = " wayland"
BB_MULTI_PROVIDER_ALLOWED:append:rcar-gen4 = " virtual/libgl virtual/egl virtual/libgles1 virtual/libgles2"
PREFERRED_PROVIDER_virtual/libgles1:rcar-gen4 = ""
PREFERRED_PROVIDER_virtual/libgles2:rcar-gen4 = "gles-user-module"
PREFERRED_PROVIDER_virtual/egl:rcar-gen4 = "libegl"
PREFERRED_PROVIDER_virtual/libgl:rcar-gen4 = ""
PREFERRED_PROVIDER_virtual/mesa:rcar-gen4 = ""
PREFERRED_PROVIDER_virtual/libgbm:rcar-gen4 = "libgbm"
PREFERRED_PROVIDER_libgbm-dev:rcar-gen4 = "libgbm"
BBMASK:append:rcar-gen4 = " mesa-gl"

# To build gstreamer plugins
LICENSE_FLAGS_ACCEPTED:rcar-gen4 = "commercial"

# Need to force 6.12 since kernel-module-gles fails to build with 6.18
PREFERRED_VERSION_linux-renesas:rcar-gen4 = "6.12%"

BBMASK += " \
    meta-sparrow-hawk/recipes-core/base-files/base-files_%.bbappend \
    meta-sparrow-hawk/recipes-core/systemd/ \
"
