# Required for weston startup with polkit in DISTRO_FEATURES
IMAGE_INSTALL += "${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)}"

# NOTE: In hardknott or later this can be replaced by adding "weston"
#       to IMAGE_FEATURES.
SYSTEMD_DEFAULT_TARGET = "graphical.target"
