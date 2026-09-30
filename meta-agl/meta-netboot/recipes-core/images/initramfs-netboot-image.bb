LICENSE = "MIT"
require ${@bb.utils.contains('NETBOOT_ENABLED', '1', 'initramfs-netboot-image_netboot.inc', '', d)}
