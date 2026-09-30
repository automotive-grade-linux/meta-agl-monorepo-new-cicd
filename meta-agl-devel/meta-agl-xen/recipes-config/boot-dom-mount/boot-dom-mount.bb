DESCRIPTION = "AGL Xen dom boot partition mount"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "file://boot-dom.mount.in"

S = "${UNPACKDIR}"

inherit allarch

# Assume using SD card
DOM_BOOT_PARTITION ?= "/dev/mmcblk0p2"

do_configure[noexec] = "1"

do_compile() {
    sed -e "s|@PARTITION@|${DOM_BOOT_PARTITION}|g" \
	${UNPACKDIR}/boot-dom.mount.in > ${WORKDIR}/boot-dom.mount
}

do_install() {
    install -d  ${D}${systemd_system_unitdir}/
    install -D -m 0644 ${WORKDIR}/boot-dom.mount ${D}${systemd_system_unitdir}/
    install -d ${D}${systemd_system_unitdir}/sysinit.target.wants/
    ln -s ../boot-dom.mount ${D}${systemd_system_unitdir}/sysinit.target.wants/boot-dom.mount
}

FILES:${PN} += "${systemd_system_unitdir}"
