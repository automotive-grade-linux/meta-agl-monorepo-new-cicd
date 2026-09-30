SUMMARY = "camera_streams_app"
DESCRIPTION = "Demonstrates camera streams received from inference models 'cam_infer_models'"
AUTHOR = "Amr Elkenawy"
HOMEPAGE = "https://github.com/AElkenawy/camera_streams_app"
BUGTRACKER = "https://github.com/AElkenawy/camera_streams_app/issues"
SECTION = "graphics"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c6f26d7ac5ed475768ab7989a17ceaf3"

SRCREV = "3b1b38a7e29e858d66d4da39a4ab4fdd67e59885"
SRC_URI = "gitsm://github.com/AElkenawy/camera_streams_app.git;lfs=0;branch=main;protocol=https;destsuffix=camera-streams-app"

S = "${WORKDIR}/camera-streams-app"

inherit flutter-app agl-app

# flutter-app
#############
PUBSPEC_APPNAME = "camera_streams_app"
FLUTTER_BUILD_ARGS = "bundle -v"
PUBSPEC_IGNORE_LOCKFILE = "1"

# agl-app
#########
AGL_APP_TEMPLATE = "agl-app-flutter"
AGL_APP_NAME = "Camera-Streams"
AGL_APP_ID = "camera_streams_app"
