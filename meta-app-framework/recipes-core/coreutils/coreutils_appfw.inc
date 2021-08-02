# Smack patches are included in coreutils v8.22, we just need to enable them.
# The default is not deterministic (enabled if libsmack found), so disable
# explicitly otherwise.
EXTRA_OECONF_SMACK:class-target = "--disable-libsmack"
EXTRA_OECONF_SMACK:with-lsm-smack:class-target = "--enable-libsmack"
EXTRA_OECONF:append:class-target = " ${EXTRA_OECONF_SMACK}"
DEPENDS:append:with-lsm-smack:class-target = " smack"
