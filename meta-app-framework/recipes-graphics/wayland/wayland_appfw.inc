FILESEXTRAPATHS_prepend := "${THISDIR}/wayland:"

SRC_URI_append = "\
    file://0001-Change-socket-mode-add-rw-for-group.patch \
    "
