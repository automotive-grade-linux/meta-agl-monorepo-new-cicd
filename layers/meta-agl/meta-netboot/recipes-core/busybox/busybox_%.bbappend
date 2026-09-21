require ${@bb.utils.contains('NETBOOT_ENABLED', '1', 'busybox_netboot.inc', '', d)}
