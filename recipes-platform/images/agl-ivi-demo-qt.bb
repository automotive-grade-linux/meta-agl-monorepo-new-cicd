require agl-ivi-image.bb

SUMMARY = "AGL IVI demo Qt image"

KUKSA_CONF = "kuksa-conf"

# import default music data package if PREINSTALL_MUSIC is set to "1"
MUSICDATA ?= "${@oe.utils.conditional("PREINSTALL_MUSIC", "1", "pre-install-music-data", "", d)}"

AGL_APPS_INSTALL += " \
    dashboard \
    hvac \
    ondemandnavi \
    settings \
    mediaplayer \
    messaging \
    phone \
    radio \
    ${KUKSA_CONF} \
    window-management-client-grpc \
    camera-gstreamer \
    agl-shell-activator \
"

IMAGE_INSTALL += " \
    packagegroup-agl-demo-platform \
    weston-terminal-conf \
    ${MUSICDATA} \
"

