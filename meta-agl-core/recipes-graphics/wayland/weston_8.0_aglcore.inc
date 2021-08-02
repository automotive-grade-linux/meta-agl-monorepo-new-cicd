FILESEXTRAPATHS:append := ":${THISDIR}/weston"

SRC_URI:append = "\
    file://0005-correctly-tear-down-drm-backend.patch \
    "

# Workaround for incorrect upstream definition
PACKAGECONFIG[remoting] = "-Dremoting=true,-Dremoting=false,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[remoting] = "-Dremoting=true,-Dremoting=false,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[headless] = "-Dbackend-headless=true"
PACKAGECONFIG:append = "${@bb.utils.contains('DISTRO_FEATURES', 'weston-remoting', ' remoting', '', d)}"
PACKAGECONFIG:append = "${@bb.utils.contains('AGL_FEATURES', 'waltham-remoting', ' remoting', '', d)}"

# seat management is only for waltham-transmitter-plugin
SRC_URI:append = "${@bb.utils.contains('AGL_FEATURES', 'waltham-remoting', 'file://0001-libweston-Migrate-weston_seat_init-release-to-public.patch', '', d)}"
