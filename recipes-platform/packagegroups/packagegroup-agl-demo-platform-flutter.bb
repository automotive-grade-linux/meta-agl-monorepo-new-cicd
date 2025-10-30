SUMMARY = "The software for Flutter Demo platform of AGL IVI profile"
DESCRIPTION = "A set of packages for AGL Flutter Demo Platform"

LICENSE = "MIT"

inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = "\
    packagegroup-agl-demo-platform-flutter \
    "

RDEPENDS:${PN} += "\
    packagegroup-agl-demo \
    "

RDEPENDS:${PN}:append = " \
    agl-compositor \
    flutter-auto \
    agl-flutter-env \
    applaunchd-template-agl-app-flutter \
    psplash-portrait-config \
    "

# set FLUTTER_CAMERA_ENABLE to "1" to enable
FLUTTER_CAMERA_STREAMS_ENABLE ??= "0"
FLUTTER_CAMERA_PACKAGES = "camera-infer-models  flutter-camera-streams-app"

RDEPENDS:${PN}:append = " ${@bb.utils.contains('FLUTTER_CAMERA_STREAMS_ENABLE', '1', '${FLUTTER_CAMERA_PACKAGES}', '', d)}"
