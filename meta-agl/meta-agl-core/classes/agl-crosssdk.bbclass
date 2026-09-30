IMAGE_FEATURES += "dev-pkgs"
IMAGE_INSTALL += "kernel-dev kernel-devsrc"

inherit populate_sdk

# Task do_populate_sdk and do_rootfs can't be exec simultaneously.
# Both exec "createrepo" on the same directory, and so one of them
# can failed (randomly).
addtask do_populate_sdk after do_rootfs
