PACKAGECONFIG:append = " \
    aio \
    libusb \
    ${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)} \
"
