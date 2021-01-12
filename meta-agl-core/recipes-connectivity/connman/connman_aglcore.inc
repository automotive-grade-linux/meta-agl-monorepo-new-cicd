FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://0001-disable-when-booting-over-nfs.patch \
            file://main.conf \
"

FILES_${PN} += "${sysconfdir}/connman/main.conf"

PACKAGECONFIG[session-policy-local] = "--enable-session-policy-local,--disable-session-policy-local,"
PACKAGECONFIG_append = " session-policy-local"

do_install_append() {
    install -d ${D}${sysconfdir}/connman
    install -m 0644 ${WORKDIR}/main.conf ${D}${sysconfdir}/connman
}
