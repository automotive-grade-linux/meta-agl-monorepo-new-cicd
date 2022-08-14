FILESEXTRAPATHS:prepend := "${THISDIR}/qtbase:"

SRC_URI:append = " \
    file://0001-Don-t-use-a-deprecated-function-if-built-linked-with.patch \
    file://0002-Adapt-for-q_EVP_PKEY_base_id-q_EVP_PKEY_get_base_id-.patch \
"

PACKAGECONFIG_WAYLAND = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)}"

PACKAGECONFIG_GL = "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gles2', '', d)}"

PACKAGECONFIG:append = " ${PACKAGECONFIG_WAYLAND}"

# '-qpa wayland-egl' set wayland-egl as default of platform plugins
PACKAGECONFIG[wayland]="-qpa wayland-egl -no-qpa-platform-guard"
