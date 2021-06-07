PACKAGECONFIG_WAYLAND = "${@bb.utils.contains('DISTRO_FEATURES', 'wayland', 'wayland', '', d)}"

PACKAGECONFIG_GL = "${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'gles2', '', d)}"

PACKAGECONFIG_append = " ${PACKAGECONFIG_WAYLAND}"

# '-qpa wayland-egl' set wayland-egl as default of platform plugins
PACKAGECONFIG[wayland]="-qpa wayland-egl -no-qpa-platform-guard"
