DISTRO_FEATURES:append = " virtualization xen"

#
# Unfortunately, using any of the expanded overrides or e.g. TUNE_ARCH
# from the host MACHINE here seems not feasible due to BitBake conf
# file parse ordering, so a generic mapping to e.g. virtio-${TUNE_ARCH}
# is sadly not currently workable.
#

# Set default guest machine
AGL_GUEST_MACHINE = "virtio-aarch64"

# Hook to allow overriding AGL_GUEST_MACHINE for specific targets
include agl-xen-${MACHINE}.inc

BBMULTICONFIG ?= "agl-xen-domu"

AGL_FEATURES:append = " agl-xen"

# Hook to for reusable multiconfig vs default configuration.
include ${@"mc-conf-multiconfig.inc" if "${BB_CURRENT_MC}" != "" else "mc-conf-default.inc"}

