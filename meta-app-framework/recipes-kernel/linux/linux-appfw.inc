FILESEXTRAPATHS_prepend := "${THISDIR}/linux:"

# Enable SMACK support without making it the default explicitly.
AGL_KCONFIG_FRAGMENTS += "smack.cfg"

# Enable SMACK as default LSM, can be overridden by changing this
# variable to e.g. "".
#
# NOTE:
# We use a different fragment for kernels older than 5.x that predate
# the switch to using CONFIG_LSM instead of CONFIG_DEFAULT_SECURITY.
# For simplicity, logic to handle the change being made in 5.1 instead
# of 5.0 has been omitted; in practice this should not be a problem
# since no current BSPs have been seen that use 5.0.x.  If a BSP
# kernel recipe does not set LINUX_VERSION, the kernel being 5.x or
# newer is assumed as the default behavior.
LINUX_VERSION_MAJOR = "${@(d.getVar('LINUX_VERSION') or "5.x").split('.')[0]}"
SMACK_DEFAULT_SUFFIX = "${@'' if int(d.getVar('LINUX_VERSION_MAJOR') or 0) >= 5 else '-old'}"
SMACK_DEFAULT_SECURITY ??= "smack-default-lsm${SMACK_DEFAULT_SUFFIX}.cfg"
AGL_KCONFIG_FRAGMENTS += "${SMACK_DEFAULT_SECURITY}"

# Enable audit support
AGL_KCONFIG_FRAGMENTS += "audit.cfg"
