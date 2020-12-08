require ${@bb.utils.contains('APPFW_ENABLED', '1', 'packagegroup-agl-image-minimal_appfw.inc', '', d)}
