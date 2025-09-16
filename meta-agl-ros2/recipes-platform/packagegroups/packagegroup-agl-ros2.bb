SUMMARY = "AGL ROS2 feature runtime packages"
DESCRIPTION = "This package group includes runtime AGL feature packages \
               for running ROS2 applications"

LICENSE = "Apache-2.0"

inherit packagegroup

PACKAGES = "\
    packagegroup-agl-ros2 \
"

RDEPENDS:${PN} = " \
    ros2-humble-ldconfig \
"
