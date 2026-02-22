require ${@bb.utils.contains('AGL_FEATURES', 'agl-kvm', 'linux_aglkvm.inc', '', d) if bb.data.inherits_class('kernel', d) else ''}
