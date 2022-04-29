require conf/include/agl_rcar-nogfx.inc
SOC_FAMILY = "r8a7796"
BOARD_NAME = "m3ulcb"

DTB_SUFFIX = "r8a77960-ulcb"

# Mask graphic Pkgs
BBMASK += "gles-user-module|kernel-module-gles|wayland-kms|libgbm"
# Mask MMP recipes
BBMASK += "kernel-module-uvcs-drv|omx-user-module"

# Work around use of obsolete Weston backend-fbdev in meta-rcar-gen3
# when building without the proprietary driver stack.  Masking out the
# weston_%.bbappend seems the simplest approach for now, as forcing
# USE_GLES = "1" to disable the adding of -Dbackend-default="fbdev" to
# EXTRA_OEMESON triggers adding "libgbm" to DEPENDS, which breaks due
# to that being a component specific to the proprietary stack we are
# explicitly disabling.  If the "libgbm" usage in the bbappend was
# instead "virtual/libgbm" as is now used in poky, it might be possible
# to just tweak PREFERRED_PROVIDER_virtual/libgbm = "mesa".
BBMASK += "meta-rcar-gen3/recipes-graphics/wayland/weston_%.bbappend"

# do use software rendering to display the compositor
# used for images w/o binary-only driver
WESTON_USE_PIXMAN = "1"

DISTRO_FEATURES:append = "${@bb.utils.contains('AGL_XEN_WANTED', '1', ' xen', '', d)}"
IMAGE_INSTALL:append = "${@bb.utils.contains('AGL_XEN_WANTED', '1', ' xen-base xen-mkimage', '', d)}"
