FILESEXTRAPATHS:prepend := "${THISDIR}/qtbase:"

SRC_URI:append = " file://0001-Revert-rhi-gl-Maintain-Qt-5-compat-wrt-disk-cache-be.patch"

QT_QPA_DEFAULT_PLATFORM = "wayland-egl"
