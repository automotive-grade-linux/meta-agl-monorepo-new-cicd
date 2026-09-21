SUMMARY = "The middlewares for AGL IVI profile"
DESCRIPTION = "Hardware-specific packages required by Multimedia Subsystem"
LICENSE = "MIT"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

PACKAGES = "\
    packagegroup-agl-ivi-multimedia-hardware \
    "

ALLOW_EMPTY:${PN} = "1"

RDEPENDS:${PN} = ""

RDEPENDS:${PN}:append:rcar-gen3 = "\
    ${@bb.utils.contains('MACHINE_FEATURES', 'multimedia', 'packagegroup-multimedia-kernel-modules', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'multimedia', 'packagegroup-multimedia-libs', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'multimedia', 'packagegroup-gstreamer1.0-plugins', '', d)} \
    "
