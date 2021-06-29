require ${@bb.utils.contains('AGL_FEATURES', 'aglcore', 'qtwayland_aglcore.inc', '', d)}
