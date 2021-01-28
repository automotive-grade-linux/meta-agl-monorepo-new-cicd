FILESEXTRAPATHS_prepend := "${THISDIR}/rng-tools:"

SRC_URI += " \
           file://0001-rngd_jitter-fix-O_NONBLOCK-setting-for-entropy-pipe.patch \
           file://0002-rngd_jitter-initialize-AES-key-before-setting-the-en.patch \
           file://0003-rngd_jitter-always-read-from-entropy-pipe-before-set.patch \
	   "

