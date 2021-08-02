# enable security features (smack, cynagora) - required by Application Framework
OVERRIDES .= ":with-lsm-smack"
DISTRO_FEATURES:append = " smack xattr"
DISTRO_FEATURES_NATIVE:append = " smack xattr"

APPFW_ENABLED = "1"

# use tar-native to support SMACK extended attributes independently of host config
IMAGE_CMD_TAR = "tar --xattrs --xattrs-include='*'"
do_image_tar[depends] += "tar-replacement-native:do_populate_sysroot"
EXTRANATIVEPATH += "tar-native"

# security: enable ssh server in place of dropbear to support PAM on user sessions
IMAGE_FEATURES += "ssh-server-openssh"

# enforce copy of xattrs (to be removed, see SPEC-475)
PACKAGECONFIG:append:pn-shadow = " attr"
PACKAGECONFIG:append:pn-shadow-native = " attr"

# set the home directory for root
ROOT_HOME = "/home/0"

# include devel wgts in images
IMAGE_FEATURES:append = " agl-devel-wgt"
