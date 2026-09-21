SUMMARY = "PipeWire Media Server"
DESCRIPTION = "The set of packages required to use PipeWire in AGL"
LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-pipewire \
    packagegroup-pipewire-tools \
    "

RDEPENDS:${PN} += "\
    pipewire \
    pipewire-modules-meta \
    pipewire-spa-plugins-meta \
    pipewire-alsa \
    gstreamer1.0-pipewire \
    wireplumber \
"

RDEPENDS:${PN}-tools += "\
    pipewire-tools \
    pipewire-spa-tools \
"