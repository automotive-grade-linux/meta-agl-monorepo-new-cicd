require ${@bb.utils.contains('AGL_FEATURES', 'aglcore', 'packagegroup-agl-graphical-weston_aglcore.inc', '', d)}
