require ${@bb.utils.contains('AGL_FEATURES', 'agl-drm-lease', 'drm-lease-manager-init_agl-drm-lease.inc', '', d)}
