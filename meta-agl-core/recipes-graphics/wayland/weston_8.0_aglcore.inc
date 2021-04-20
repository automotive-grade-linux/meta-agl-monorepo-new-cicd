FILESEXTRAPATHS_append := ":${THISDIR}/weston"

SRC_URI_append = "\
    file://0001-libweston-Migrate-weston_seat_init-release-to-public.patch \
    file://0005-correctly-tear-down-drm-backend.patch \
    "

# Workaround for incorrect upstream definition
PACKAGECONFIG[remoting] = "-Dremoting=true,-Dremoting=false,gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG_append = "${@bb.utils.contains('DISTRO_FEATURES', 'weston-remoting', ' remoting', '', d)}"
PACKAGECONFIG_append = "${@bb.utils.contains('AGL_FEATURES', 'waltham-remoting', ' remoting', '', d)}"
