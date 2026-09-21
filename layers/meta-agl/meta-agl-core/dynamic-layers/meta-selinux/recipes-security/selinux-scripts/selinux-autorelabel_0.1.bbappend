require ${@bb.utils.contains('AGL_FEATURES', 'aglcore', 'selinux-autorelabel_aglcore.inc', '', d)}
