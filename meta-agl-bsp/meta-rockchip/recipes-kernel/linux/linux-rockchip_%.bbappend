require recipes-kernel/linux/linux-yocto-agl.inc

AGL_KCONFIG_FRAGMENTS += " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'virtualization', 'kvm.cfg', '', d)} \
    "
