# If weston's PACKAGECONFIG contains systemd + pam + polkit, polkit
# ends up a hard requirement for start up due to the pam + systemd
# configuration that is being installed.  For now, keep things simple
# and assume that polkit should be a dependency if it is in
# DISTRO_FEATURES.
RDEPENDS:${PN} += "${@bb.utils.filter('DISTRO_FEATURES', 'polkit', d)}"
