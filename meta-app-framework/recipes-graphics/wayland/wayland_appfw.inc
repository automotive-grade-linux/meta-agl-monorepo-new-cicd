FILESEXTRAPATHS:prepend := "${THISDIR}/wayland:"

SRC_URI:append = "\
    file://0001-Change-socket-mode-add-rw-for-group.patch \
    "
