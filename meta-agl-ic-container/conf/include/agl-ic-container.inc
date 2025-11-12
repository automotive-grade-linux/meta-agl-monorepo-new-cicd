BBMULTICONFIG = "agl-container-cluster agl-container-ivi"

# The meta-virtualization has a sanity-meta-virt check.
# This checker check 'DISTRO_FEATURES has virtualization', but 
# in case of linux container is not necessary to the 'virtualization'.
# This should be revisited when upgrading past dunfell, as less of 
# the extra configuration may be enabled in the future.
INHERIT:remove = "sanity-meta-virt"

include mc-conf-${BB_CURRENT_MC}.inc

# network manager to use
VIRTUAL-RUNTIME_net_manager = "systemd"

# Enable demo gstreamer plugin usage
LICENSE_FLAGS_ACCEPTED += "commercial_gstreamer1.0-plugins-ugly commercial_gstreamer1.0-libav commercial_ffmpeg"
