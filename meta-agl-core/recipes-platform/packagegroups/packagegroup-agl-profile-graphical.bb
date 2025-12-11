SUMMARY = "The middleware for AGL IVI profile"
DESCRIPTION = "The base set of packages required for a AGL IVI Distribution"
LICENSE = "MIT"

inherit packagegroup

# Reuse include file from upstream weston since we have the same requirements
require recipes-graphics/wayland/required-distro-features.inc

PACKAGES = "\
    packagegroup-agl-profile-graphical \
    profile-graphical \
"

RDEPENDS:${PN} += "\
    packagegroup-agl-image-minimal \
    packagegroup-agl-graphical-compositor \
"

RDEPENDS:profile-graphical = "${PN}"
