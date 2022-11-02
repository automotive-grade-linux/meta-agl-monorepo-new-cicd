FILESEXTRAPATHS:prepend := "${THISDIR}/systemd:"

SRC_URI += "\
    file://e2fsck.conf \
    ${@bb.utils.contains('VIRTUAL-RUNTIME_net_manager','systemd','file://wired.network','',d)} \
"

# enable networkd/resolved support
PACKAGECONFIG:append:pn-systemd = " \
   ${@bb.utils.contains('VIRTUAL-RUNTIME_net_manager','systemd','networkd resolved','',d)} \
"

do_install:append() {
    # Install /etc/e2fsck.conf to avoid boot stuck by wrong clock time
    install -m 644 -p -D ${WORKDIR}/e2fsck.conf ${D}${sysconfdir}/e2fsck.conf

    if ${@bb.utils.contains('VIRTUAL-RUNTIME_net_manager','systemd','true','false',d)}; then
       # Install DHCP configuration for Ethernet adapters
       install -m 644 ${WORKDIR}/wired.network ${D}${sysconfdir}/systemd/network
    fi
}

FILES:${PN} += "${sysconfdir}/e2fsck.conf "

# SPEC-737: connmand also has a NTP client which races with systemd-timesyncd
PACKAGECONFIG:remove = "timesyncd"

# Enable systemd-coredump when agl-devel is set on
PACKAGECONFIG:append:agl-devel = " coredump"
