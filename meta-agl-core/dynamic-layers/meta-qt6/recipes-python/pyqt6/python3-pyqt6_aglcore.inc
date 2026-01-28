# Work around assumption that Vulkan is enabled
DISABLED_FEATURES += "${@bb.utils.contains('DISTRO_FEATURES', 'vulkan', '', 'PyQt_Vulkan', d)}"