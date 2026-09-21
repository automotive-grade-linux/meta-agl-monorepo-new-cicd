SUMMARY = "SELinux packages"
DESCRIPTION = "SELinux packages required for AGL"
LICENSE = "MIT"

inherit packagegroup features_check

REQUIRED_DISTRO_FEATURES = "selinux"

PACKAGES = " \
    packagegroup-agl-core-selinux \
    packagegroup-agl-core-selinux-devel \
"

#
# meta-selinux's packagegroup-core-selinux includes a lot of
# policy development tools with its inclusion of the layer's
# packagegroup-selinux-policycoreutils, which is not really
# desirable for a production image.  Create our own base
# packagegroup and an accompanying devel packagegroup that
# agl-devel can trigger pulling in.
#
# NOTES:
# - It seems likely we will always want auditd, so include
#   it in the base packagegroup.
# - selinux-autorelabel seems required to handle both the
#   edge case of builds done on non-xattr capable filesystems,
#   and to allow driving relabeling after potential package
#   installation during runtime.
# - packagegroup-selinux-policycoreutils includes a lot of
#   things that seem not useful in a lot of systems (e.g.
#   the gtk dependent selinux-gui), so for now the devel
#   packagegroup aims to include a more minimal set of tools
#   aimed at enabling checkpolicy and audit2allow use.
# - Some thought needs to go into whether the relabeling
#   fixup packages should be handled separately, as they
#   ideally should not go into images using read-only or
#   stateless rootfs, but those are image features so we
#   cannot check for them here.
#

RDEPENDS:${PN} = " \
    packagegroup-selinux-minimal \
    auditd \
    selinux-autorelabel \
    systemd-selinux-relabel \
"

RDEPENDS:${PN}-devel = " \
    ${BPN} \
    libsepol-bin \
    checkpolicy \
    policycoreutils-loadpolicy \
    policycoreutils-setsebool \
    policycoreutils-hll \
    semodule-utils-semodule-package \
    selinux-python-audit2allow \
"
