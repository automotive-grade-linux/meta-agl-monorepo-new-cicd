require conf/include/agl_rcar-nogfx.inc
SOC_FAMILY = "r8a7795"
BOARD_NAME = "h3ulcb"

DTB_SUFFIX = "r8a77951-ulcb"

# Mask graphic Pkgs
BBMASK += "gles-user-module|kernel-module-gles|wayland-kms|libgbm"
# Mask MMP recipes
BBMASK += "kernel-module-uvcs-drv|omx-user-module"

# Workaround for SPEC-3706 - versioned bbappends and upstream moved on to 1.16.3
BBMASK += "meta-renesas/meta-rcar-gen3/recipes-multimedia/gstreamer/gstreamer1.0-.*1.16.2\.bbappend"

# do use software rendering to display the compositor
# used for images w/o binary-only driver
WESTON_USE_PIXMAN = "1"
