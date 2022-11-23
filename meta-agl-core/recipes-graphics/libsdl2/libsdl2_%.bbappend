require ${@bb.utils.contains('AGL_FEATURES', 'aglcore', 'libsdl2_aglcore.inc', '', d)}
