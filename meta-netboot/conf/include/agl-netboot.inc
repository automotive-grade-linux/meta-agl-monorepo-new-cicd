INHERIT += "netboot"

IMAGE_INSTALL:append:netboot = " \
    curl \
    ${@bb.utils.contains('DISTRO_FEATURES', 'selinux', 'resolv-conf-relabel policycoreutils-loadpolicy', '', d)} \
"
