FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

ADD_VULKAN = "${@bb.utils.contains("DISTRO_FEATURES", "vulkan", " backend-wayland-vulkan", "", d)}"
REMOVE_EGL = "${@bb.utils.contains("DISTRO_FEATURES", "vulkan", "backend-wayland-egl", "", d)}"

PACKAGECONFIG:append:raspberrypi4 = "${ADD_VULKAN}"
PACKAGECONFIG:remove:raspberrypi4 = "${REMOVE_EGL}"

PACKAGECONFIG:append:raspberrypi5 = "${ADD_VULKAN}"
PACKAGECONFIG:remove:raspberrypi5 = "${REMOVE_EGL}"

PACKAGECONFIG:append:rk3588s = "${ADD_VULKAN}"
PACKAGECONFIG:remove:rk3588s = "${REMOVE_EGL}"
