FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

EGLCONFIG_FALLBACK = "${@bb.utils.contains_any('AGL_FEATURES', 'agl-rdp agl-kvm-guest', '', 'file://0001-backend-wayland_egl-Add-a-fallback-eglConfig.patch', d)}"
SRC_URI:append:use-nxp-bsp = " file://0001-Disable-on_frame_base_surface-wl_surface_commit.patch"
SRC_URI:append = " ${EGLCONFIG_FALLBACK}"

# necessary to avoid crashing flutter-auto when a key keyboard event is emitted
SRC_URI:append = " file://0001-flutter_desktop-Remove-asio-post-call.patch"

# Disable WIP webview plugin on 32-bit ARM platforms for now, as build
# failures have been seen on qemuarm and beaglebone.
PACKAGECONFIG:remove:arm = "webview_flutter_view"
