# AGL base image class
#
# As opposed to using core-image directly, this class:
# - does not pull in packagegroup-base-extended by default to minimize images.
#   This does mean that many of the oe-core / poky MACHINE and DISTRO features
#   (e.g. 3g, nfs, etc.) will not result in packages being automatically pulled
#   into images since that is driven via packagegroup-base.
# - includes hooks for integrating SELinux via meta-selinux
# - disables locale installation by default
#

# Disable locales
IMAGE_LINGUAS = ""

# Hook to allow inheriting e.g. selinux-image by default.
# Any bbclass used as a value should ultimately inherit core-image
AGL_BASE_IMAGE ?= "core-image"

inherit ${AGL_BASE_IMAGE}

FEATURE_PACKAGES_selinux = " \
    packagegroup-agl-core-selinux \
    ${@bb.utils.contains('DISTRO_FEATURES', 'agl-devel', 'packagegroup-agl-core-selinux-devel', '', d)} \
"

IMAGE_FEATURES:append = " ${@bb.utils.filter('DISTRO_FEATURES', 'selinux', d)}"

CORE_IMAGE_BASE_INSTALL = " \
    packagegroup-agl-core-boot \
    \
    ${CORE_IMAGE_EXTRA_INSTALL} \
"
