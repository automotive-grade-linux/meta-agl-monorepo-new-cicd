require ${@bb.utils.contains('APPFW_ENABLED', '1', 'run-postinsts_appfw.inc', '', d)}
