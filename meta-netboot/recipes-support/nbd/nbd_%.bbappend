require ${@bb.utils.contains('NETBOOT_ENABLED', '1', 'nbd_netboot.inc', '', d)}
