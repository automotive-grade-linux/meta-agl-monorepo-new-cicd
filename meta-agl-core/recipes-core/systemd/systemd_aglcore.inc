FILESEXTRAPATHS:prepend := "${THISDIR}/systemd:"

SRC_URI += "\
    file://e2fsck.conf \
    file://wait-disable.conf \
    file://wired.network \
"

# Enable networkd/resolved support if using systemd for network management
PACKAGECONFIG:append = " \
   ${@bb.utils.contains('VIRTUAL-RUNTIME_net_manager','systemd','networkd resolved','',d)} \
   iptc \
"

# SPEC-737: connmand also has a NTP client which races with systemd-timesyncd
PACKAGECONFIG:remove = "timesyncd"

# Enable systemd-coredump when agl-devel is set on
PACKAGECONFIG:append:agl-devel = " coredump"

do_install:append() {
    # Install /etc/e2fsck.conf to avoid boot stuck by wrong clock time
    install -m 644 -p -D ${WORKDIR}/e2fsck.conf ${D}${sysconfdir}/e2fsck.conf

    if [ "${VIRTUAL-RUNTIME_net_manager}" = "systemd" ]; then
       # Install DHCP configuration for Ethernet adapters
       install -m 644 ${WORKDIR}/wired.network ${D}${sysconfdir}/systemd/network
    elif [ "${VIRTUAL-RUNTIME_net_manager}" = "connman" ]; then
       # Disable systemd-networkd-wait-online by default
       install -d ${D}${systemd_system_unitdir}/systemd-networkd-wait-online.service.d
       install -m 0644 ${WORKDIR}/wait-disable.conf ${D}${systemd_system_unitdir}/systemd-networkd-wait-online.service.d/
    fi
}

FILES:${PN} += "${sysconfdir}/e2fsck.conf "

