require agl-ivi-image-flutter.bb

SUMMARY = "AGL IVI demo Flutter image"

KUKSA_CONF = "kuksa-conf"

# import default music data package if PREINSTALL_MUSIC is set to "1"
MUSICDATA ?= "${@oe.utils.conditional("PREINSTALL_MUSIC", "1", "pre-install-music-data", "", d)}"

AGL_APPS_INSTALL += " \
    flutter-ics-homescreen \
    ${KUKSA_CONF} \
    camera-gstreamer \
    window-management-client-grpc \
    agl-shell-activator \
    ondemandnavi \
    ${MUSICDATA} \
"
