SUMMARY = "Camera Inference Models"
DESCRIPTION = "Camera Inference Process which uses Pipewire."
AUTHOR = "Amr Elkenawy"
HOMEPAGE = "https://github.com/AElkenawy/cam_infer_models"
BUGTRACKER = "https://github.com/AElkenawy/cam_infer_models/issues"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c6f26d7ac5ed475768ab7989a17ceaf3"

DEPENDS += "\
    grpc \
    grpc-native \
    opencv \
    pipewire \
    tensorflow-lite \
    "

SRCREV = "9e7a5057aa89d228ac679ac1ef14641a1cff3cee"
SRC_URI = "gitsm://github.com/AElkenawy/cam_infer_models.git;protocol=https;branch=jw/cmake-refactor;destsuffix=cam-infer-models"
S = "${WORKDIR}/cam-infer-models"


inherit cmake pkgconfig

EXTRA_OECMAKE += " \
    -DTFLITE_ROOT=${STAGING_DIR_TARGET}/usr/include \
    -DTFLITE_LIBRARY=${STAGING_DIR_TARGET}/usr/lib/libtensorflowlite.so \
    "
