MACHINE = "aws-ec2-arm64"

VIRTUAL-RUNTIME_initscripts = "systemd-compat-units"
IMAGE_INSTALL:append = " cloud-init"
DISTRO_FEATURES:append = " virtualization"


# can we shuffle this out so CI still works
INHERIT += "extrausers"
# Hardening: Locking the root password. Creating the user without password for ssh key-based login only
EXTRA_USERS_PARAMS = "usermod -L root; useradd -p '*' user"

EXTRA_IMAGE_FEATURES:append = " ssh-server-openssh"

# Forcing removal of debug-tweakes as that leads to reversing some sshd_config hardening done in our bbappend when do_rootfs runs
EXTRA_IMAGE_FEATURES:remove = "debug-tweaks"

AGL_DEFAULT_IMAGE_FSTYPES := "wic.vhd"

# workaround bug in cloud-init leading to empty cloud-init-systemd package
PACKAGES:pn-cloud-init:forcevariable = "cloud-init-src cloud-init-dbg cloud-init-staticdev cloud-init-dev cloud-init-doc cloud-init-locale  cloud-init-systemd cloud-init"
