DESCRIPTION = "The minimal set of packages required for the AGL compositor"
LICENSE = "MIT"

inherit packagegroup features_check

# agl-compositor-init requires pam enabled if started via systemd
REQUIRED_DISTRO_FEATURES = "wayland ${@oe.utils.conditional('VIRTUAL-RUNTIME_init_manager', 'systemd', 'pam', '', d)}"

RDEPENDS:${PN} += " \
    agl-compositor \
    agl-compositor-init \
    ${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)} \
"
