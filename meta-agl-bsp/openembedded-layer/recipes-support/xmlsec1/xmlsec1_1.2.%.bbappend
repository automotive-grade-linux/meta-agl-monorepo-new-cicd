FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

# 2021-08-15:
# Fix a host contamination
# Submitted upstream as backport from meta-oe-master
# https://lists.openembedded.org/g/openembedded-devel/message/92583

SRC_URI += "file://ensure-search-path-non-host.patch"

