FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://remove-redundant-yyloc-global.patch "
