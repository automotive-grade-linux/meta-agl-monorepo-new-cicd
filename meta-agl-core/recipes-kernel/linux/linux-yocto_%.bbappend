require ${@bb.utils.contains('AGL_FEATURES', 'aglcore', '${BPN}-agl.inc', '', d)}
