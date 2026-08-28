FILESEXTRAPATHS:prepend := "${THISDIR}/linux:"

# Disable meta-virtualization's addition of the kernel-meta repo's xen.cfg,
# as we have our own finer-grained configuration that is hopefully more
# obvious and straightforward to modify.
KERNEL_FEATURES:remove = "cfg/xen.scc"

AGL_KCONFIG_FRAGMENTS_DOM0 = "xen-dom0.cfg xen-backends.cfg"
AGL_KCONFIG_FRAGMENTS_DOM0:append:x86-64 = " xen-dom0-x86.cfg"

AGL_KCONFIG_FRAGMENTS_DOMU = "xen-domu.cfg xen-frontends.cfg"
AGL_KCONFIG_FRAGMENTS_DOMU:append:x86-64 = " xen-domu-x86.cfg"

AGL_KCONFIG_FRAGMENTS_DOMD = "${AGL_KCONFIG_FRAGMENTS_DOMU} xen-backends.cfg"

AGL_KCONFIG_FRAGMENTS += " \
    xen-common.cfg \
    ${@bb.utils.contains('AGL_FEATURES', 'agl-xen-dom0', '${AGL_KCONFIG_FRAGMENTS_DOM0}', '', d)} \
    ${@bb.utils.contains('AGL_FEATURES', 'agl-xen-domu', '${AGL_KCONFIG_FRAGMENTS_DOMU}', '', d)} \
    ${@bb.utils.contains('AGL_FEATURES', 'agl-xen-domd', '${AGL_KCONFIG_FRAGMENTS_DOMD}', '', d)} \
"

