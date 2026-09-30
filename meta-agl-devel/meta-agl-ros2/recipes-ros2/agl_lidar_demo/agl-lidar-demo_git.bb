SUMMARY = "AGL LiDAR Demo (Flutter)"
DESCRIPTION = "Top-down LiDAR / perception dashboard rendering a ROS 2 PointCloud2 \
stream over rosbridge. Ships a recorded LiDAR rosbag for an offline demo."
AUTHOR = "Shaurya Rane"
HOMEPAGE = "https://github.com/shauryarane05/agl-lidar-hmi-app"
BUGTRACKER = "https://github.com/shauryarane05/agl-lidar-hmi-app/issues"
SECTION = "graphics"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

RECIPE_MAINTAINER = "Shaurya Rane <ssranevjti@gmail.com>"

SRCREV = "1902bc0a7dbb7e494b158e70e15636d990d5679e"
SRCREV:localdev = "${AUTOREV}"
SRC_URI = "gitsm://github.com/shauryarane05/agl-lidar-hmi-app.git;branch=main;protocol=https;destsuffix=agl-lidar-demo"

S = "${WORKDIR}/agl-lidar-demo"

inherit flutter-app agl-app

# flutter-app
PUBSPEC_APPNAME = "agl_lidar_demo"
PUBSPEC_IGNORE_LOCKFILE = "1"
FLUTTER_BUILD_ARGS = "bundle -v"

# agl-app
AGL_APP_TEMPLATE = "agl-app-flutter"
AGL_APP_NAME = "AGL LiDAR Demo"
AGL_APP_ID = "agl_lidar_demo"

do_install:append() {
    # Recorded LiDAR bag and offline demo helper (loop-play + local rosbridge).
    install -d ${D}${datadir}/${BPN}/rosbag/carla_lidar
    install -m 0644 ${S}/rosbag/carla_lidar/metadata.yaml     ${D}${datadir}/${BPN}/rosbag/carla_lidar/
    install -m 0644 ${S}/rosbag/carla_lidar/carla_lidar_0.db3 ${D}${datadir}/${BPN}/rosbag/carla_lidar/
    install -d ${D}${bindir}
    install -m 0755 ${S}/scripts/run-demo-bag.sh ${D}${bindir}/agl-lidar-demo.sh
}

FILES:${PN} += "${datadir}/${BPN}/rosbag"

RDEPENDS:${PN} += " \
    flutter-auto \
    agl-flutter-env \
    ros2cli \
    ros2launch \
    ros2bag \
    rosbag2-storage-default-plugins \
    rosbridge-server \
"
