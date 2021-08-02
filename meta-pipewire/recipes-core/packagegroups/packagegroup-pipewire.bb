SUMMARY = "PipeWire Media Server"
DESCRIPTION = "The set of packages required to use PipeWire in AGL"
LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-pipewire \
    "

RDEPENDS:${PN} += "\
    pipewire \
    pipewire-modules-meta \
    pipewire-spa-plugins-meta \
    pipewire-alsa \
    gstreamer1.0-pipewire \
    wireplumber \
    ${@bb.utils.contains('DISTRO_FEATURES', 'agl-devel', 'pipewire-tools pipewire-spa-tools', '', d)} \
"
