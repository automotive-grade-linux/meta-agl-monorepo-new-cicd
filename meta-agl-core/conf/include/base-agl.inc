# Disable kernel module tarballs by default on the assumption that
# desired modules will already be present in images.
# Comment this out to receive the separate modules tarball again.
MODULE_TARBALL_DEPLOY ?= "0"

# Comment out below if want to use QtWebkit
PACKAGECONFIG_remove_pn-qtquick1 = "webkit"


ASSUME_PROVIDED_remove = "tar-native"

# Use static IDs to enable full-filesystem updates
USERADDEXTENSION = "useradd-staticids"
USERADD_ERROR_DYNAMIC = "error"
USERADD_GID_TABLES = "files/group"
USERADD_UID_TABLES = "files/passwd"

AGL_FEATURES += "aglcore"
