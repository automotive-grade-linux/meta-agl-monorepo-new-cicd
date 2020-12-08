require ${@bb.utils.contains('APPFW_ENABLED', '1', 'libcap_appfw.inc', '', d)}
