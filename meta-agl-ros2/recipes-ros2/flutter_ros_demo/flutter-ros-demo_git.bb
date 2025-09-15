SUMMARY = "ROS Flutter Demo Application"
DESCRIPTION = "A reference ROS2 Flutter Project using rcldart library"
AUTHOR = "Saalim Quadri"
HOMEPAGE = "https://github.com/danascape/flutter-ros-demo"
BUGTRACKER = "https://github.com/danascape/flutter-ros-demo/issues"
SECTION = "graphics"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRCREV = "0b7d39fe544c385b0967dc9cb5730e3e54f35df7"
SRC_URI = "gitsm://github.com/danascape/flutter-ros-demo.git;branch=master;protocol=https;destsuffix=flutter-ros-demo"

S = "${WORKDIR}/flutter-ros-demo"

inherit flutter-app agl-app

# flutter-app
PUBSPEC_APPNAME = "flutter_ros_demo"
PUBSPEC_IGNORE_LOCKFILE = "1"
FLUTTER_BUILD_ARGS = "bundle -v"

# agl-app
AGL_APP_TEMPLATE = "agl-app-flutter"
AGL_APP_NAME = "Flutter ROS2 Demo"
AGL_APP_ID = "flutter_ros_demo"

do_install:append() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/scripts/detection_mode_controller.py ${D}${bindir}/
    install -m 0755 ${S}/scripts/driver_mood_detection.py ${D}${bindir}/
    install -m 0755 ${S}/scripts/enhanced_road_safety_camera.py ${D}${bindir}/
    install -m 0755 ${S}/scripts/launch_detection_system.py ${D}${bindir}/
}

# Python runtime dependencies for scripts
RDEPENDS:${PN} += " \
    python3-core \
    python3-numpy \
    python3-opencv \
    python3-threading \
    python3-json \
    python3-io \
    python3-logging \
    python3-fer \
    python3-dlib \
"
