require ${@bb.utils.contains('APPFW_ENABLED', '1', 'linux-appfw.inc', '', d) if bb.data.inherits_class('kernel', d) else ''}
