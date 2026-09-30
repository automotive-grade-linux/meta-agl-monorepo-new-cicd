require ${@bb.utils.contains('AGL_FEATURES', 'agl-xen', 'linux_agl-xen.inc', '', d) if bb.data.inherits_class('kernel', d) else ''}
