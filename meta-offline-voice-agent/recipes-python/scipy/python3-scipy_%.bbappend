FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://0001-Skip-f2py-version-check-during-cross-compilation.patch \
"
