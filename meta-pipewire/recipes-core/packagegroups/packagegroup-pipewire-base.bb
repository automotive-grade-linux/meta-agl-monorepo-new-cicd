SUMMARY = "PipeWire Media Server Base"
DESCRIPTION = "The set of packages required to use PipeWire API in AGL"
LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-pipewire-base \
    "

RDEPENDS:${PN} += "\
    pipewire-spa-plugins-meta \
    pipewire-modules-meta \
    ${@bb.utils.contains('DISTRO_FEATURES', 'agl-devel', 'pipewire-tools pipewire-spa-tools alsa-utils', '', d)} \
    pipewire-alsa \
"
