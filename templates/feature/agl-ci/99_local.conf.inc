DISTRO_FEATURES_append = " AGLCI"
# opencv seems to have a parallel make bug
# ...contrib/modules/xfeatures2d/test/test_features2d.cpp:51:10: fatal error: features2d/test/test_detectors_regression.impl.hpp: No such file or directory
PARALLEL_MAKE_pn-opencv = "-j 1"

AGL_HOST_PREMIRROR ?= "http://download.automotivelinux.org"
AGL_HOST_SSTATE_MIRROR ?= "http://download.automotivelinux.org"

PREMIRRORS = " \
git://.*/.*   ${AGL_HOST_PREMIRROR}/AGL/mirror/  \n \
ftp://.*/.*   ${AGL_HOST_PREMIRROR}/AGL/mirror/  \n \
http://.*/.*  ${AGL_HOST_PREMIRROR}/AGL/mirror/  \n \
https://.*/.* ${AGL_HOST_PREMIRROR}/AGL/mirror/  \n \
"

SSTATE_MIRRORS = "  file://.* ${AGL_HOST_SSTATE_MIRROR}/sstate-mirror/master/${DEFAULTTUNE}/PATH;downloadfilename=PATH \n                  "

#INHERIT += "packagefeed-stability"
INHERIT += "buildhistory"
INHERIT += "buildstats"
INHERIT += "buildstats-summary"

# setup for PRSERV and HASHSERV
AGL_HOST_PRSERV ?= "10.30.72.18"
AGL_HOST_HASHSERV ?= "10.30.72.18"

BB_HASHSERVE = "${AGL_HOST_HASHSERV}:8686"
BB_SIGNATURE_HANDLER = "OEEquivHash"

PRSERV_HOST = "${AGL_HOST_PRSERV}:8585"

IMAGE_INSTALL_append = " curl"
