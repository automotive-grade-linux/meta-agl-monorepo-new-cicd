FILESEXTRAPATHS:prepend := "${THISDIR}/qtwayland:"

# Patch reported in https://bugreports.qt.io/browse/QTBUG-79838, not upstreamed
SRC_URI += " \
           file://0001-Fix-timer-leak-and-a-potential-race.patch \
           "
