DISTRO_FEATURES:append = " acl xattr selinux"

# Reiterate the upstream default of targeted policy since that
# is the mostly widely used model, and it will likely be easier
# to pull policy from other distributions for it.
# Having an explicit setting here seems useful for documentation
# purposes, and it is still possible that using one of the other
# refpolicy package options as the AGL default desirable, and it
# would be set here.
PREFERRED_PROVIDER_virtual/refpolicy ?= "refpolicy-targeted"

# Default to permissive mode
DEFAULT_ENFORCING ?= "permissive"

# Override the base image class to get the SELinux labeling hook
AGL_BASE_IMAGE ?= "selinux-image"

# Mask out meta-selinux's linux-yocto kernel config bbappend to
# avoid collision with AGL's own more universal scheme.
BBMASK += "meta-selinux/recipes-kernel/linux/"