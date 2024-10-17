FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:use-nxp-bsp = " file://0001-Disable-on_frame_base_surface-wl_surface_commit.patch"

# Disable WIP webview plugin on 32-bit ARM platforms for now, as build
# failures have been seen on qemuarm and beaglebone.
PACKAGECONFIG:remove:arm = "webview_flutter_view"
