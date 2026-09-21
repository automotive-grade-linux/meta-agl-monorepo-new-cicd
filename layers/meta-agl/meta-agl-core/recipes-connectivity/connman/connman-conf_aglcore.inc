# Pick up our version of main.conf
FILESEXTRAPATHS:prepend := "${THISDIR}/connman-conf:"

# Upstream connman-conf only installs configuration for qemu targets
# but we need it in all our BSP
do_install:append() {
    mkdir -p ${D}${sysconfdir}/connman
    cp ${S}/main.conf ${D}${sysconfdir}/connman/main.conf
}
