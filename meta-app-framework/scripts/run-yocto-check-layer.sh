#!/bin/bash
set -x
SCRIPTPATH="$( cd $(dirname $0) >/dev/null 2>&1 ; pwd -P )"
echo $SCRIPTPATH
AGLROOT="$SCRIPTPATH/../../.."
POKYDIR="$AGLROOT/external/poky"
TMPROOT="/tmp"

rm -rf ${TMPROOT}/testbuild-ycl || true
mkdir -p ${TMPROOT}/testbuild-ycl
cd ${TMPROOT}/testbuild-ycl

source $POKYDIR/oe-init-build-env .

cat << EOF >> conf/local.conf
# just define defaults
AGL_FEATURES ?= ""
AGL_EXTRA_IMAGE_FSTYPES ?= ""

# important settings imported from poky-agl.conf
# we do not import 
DISTRO_FEATURES_append = " systemd smack"
DISTRO_FEATURES_BACKFILL_CONSIDERED_append = " sysvinit"
VIRTUAL-RUNTIME_init_manager = "systemd"

# skip unnecessary in yocto-check-layer - aka FIXME upstream
BBMASK += "meta-security/recipes-mac/smack/smack-test_1.0.bb"
BBMASK += "packagegroup-core-security-ptest.bb"

# missing in upstream recipes ... aka FIXME upstream
BBCLASSEXTEND_pn-libzip = "native nativesdk"
BBCLASSEXTEND_pn-xmlsec1 = "native nativesdk"

EOF


yocto-check-layer \
  --dependency $AGLROOT/external/meta-openembedded/meta-oe \
               $AGLROOT/external/meta-security \
               $AGLROOT/external/meta-openembedded/meta-python \
               $AGLROOT/external/meta-openembedded/meta-networking \
               $AGLROOT/external/meta-openembedded/meta-perl \
               $AGLROOT/external/meta-qt5/ \
               -- \
               $AGLROOT/meta-agl/meta-app-framework/ 

[ $? = 0 ] && rm -rf ${TMPROOT}/testbuild-ycl


