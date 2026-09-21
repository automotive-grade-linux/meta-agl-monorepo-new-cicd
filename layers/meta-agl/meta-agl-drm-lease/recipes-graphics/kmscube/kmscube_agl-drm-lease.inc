FILESEXTRAPATHS:prepend := "${THISDIR}/kmscube:"

SRC_URI += "file://0001-Add-DRM-lease-support.patch"

PACKAGECONFIG += "drm-lease"
PACKAGECONFIG[drm-lease] = "-Ddrm_lease=enabled,-Ddrm_lease=disabled,drm-lease-manager"
