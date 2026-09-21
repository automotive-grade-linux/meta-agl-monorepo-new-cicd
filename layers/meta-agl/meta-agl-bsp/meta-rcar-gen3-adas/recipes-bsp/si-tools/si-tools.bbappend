FILESEXTRAPATHS:append := ":${THISDIR}/files"

SRC_URI += " \
    file://si-tools-fm-improvements.patch \
"

EXTRA_OEMAKE:append = " 'LDFLAGS=${LDFLAGS}'"

# Setting LDFLAGS fixes the QA issue, disable INSANE_SKIP over-ride
# done in the recipe
INSANE_SKIP:${PN} = ""
INSANE_SKIP:${PN}-dev = ""