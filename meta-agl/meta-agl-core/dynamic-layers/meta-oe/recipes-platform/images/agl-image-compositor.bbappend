IMAGE_INSTALL += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'agl-devel', 'agl-shell-activator', '', d)} \
"
