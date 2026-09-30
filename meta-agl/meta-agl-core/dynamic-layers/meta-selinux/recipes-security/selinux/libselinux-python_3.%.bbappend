require ${@bb.utils.contains('AGL_FEATURES', 'aglcore', 'selinux-python_aglcore.inc', '', d)}
