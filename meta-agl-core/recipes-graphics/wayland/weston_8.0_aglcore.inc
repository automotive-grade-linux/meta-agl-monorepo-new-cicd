FILESEXTRAPATHS:append := ":${THISDIR}/weston"

SRC_URI:append = "\
    file://0002-Allow-weston-to-start-from-a-systemd-user-session.patch \
    file://0005-correctly-tear-down-drm-backend.patch \
    file://0001-clients-window-Add-set_app_id-function-change-Waylan.patch \
    file://0001-libweston-backend-drm-Re-order-gbm-destruction-at-DR.patch \
    file://0001-gl-renderer-Avoid-double-free-on-init-failure.patch \
    file://0001-libweston-Remove-source-repaint_timer-in-weston_comp.patch \
    file://0001-libweston-desktop-rename-weston_desktop_client_destr.patch \
    file://0002-libweston-desktop-introduce-weston_desktop_client_de.patch \
    file://0003-libweston-desktop-add-weston_desktop_xwayland_fini.patch \
    file://0001-libweston-add-weston_layer_fini.patch \
    "

# Workaround for incorrect upstream definition
PACKAGECONFIG[remoting] = "-Dremoting=true,-Dremoting=false,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[remoting] = "-Dremoting=true,-Dremoting=false,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[headless] = "-Dbackend-headless=true"
PACKAGECONFIG:append = "${@bb.utils.contains('DISTRO_FEATURES', 'weston-remoting', ' remoting', '', d)}"
PACKAGECONFIG:append = "${@bb.utils.contains('AGL_FEATURES', 'waltham-remoting', ' remoting', '', d)}"

# seat management is only for waltham-transmitter-plugin
SRC_URI:append = "${@bb.utils.contains('AGL_FEATURES', 'waltham-remoting', 'file://0001-libweston-Migrate-weston_seat_init-release-to-public.patch', '', d)}"
