require ${@bb.utils.contains('APPFW_ENABLED', '1', 'systemd_appfw.inc', '', d)}
