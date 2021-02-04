SUMMARY = "AGL Reference Hardware specific gpsd configuration"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "agl-refhw-h3"
PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = "file://gpsd.refhw \
           file://refhw-gpsd-helper.sh \
           file://refhw.conf \
"

inherit update-alternatives

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -D -m 0644 ${WORKDIR}/gpsd.refhw ${D}/${sysconfdir}/default/gpsd.refhw
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -D -m 0755 ${WORKDIR}/refhw-gpsd-helper.sh ${D}/${sbindir}/refhw-gpsd-helper.sh
        install -d ${D}${sysconfdir}/systemd/system/gpsd.service.d
        install -D -m 0644 ${WORKDIR}/refhw.conf ${D}${sysconfdir}/systemd/system/gpsd.service.d/refhw.conf
    fi
}

ALTERNATIVE_${PN} = "gpsd-defaults"
ALTERNATIVE_LINK_NAME[gpsd-defaults] = "${sysconfdir}/default/gpsd"
ALTERNATIVE_TARGET[gpsd-defaults] = "${sysconfdir}/default/gpsd.refhw"
# NOTE: Priority needs to be below default of 10 to avoid overriding the
#       default configuration.  The script run by the systemd drop-in
#       will tweak things on boot to handle h3ulcb vs refhw.
ALTERNATIVE_PRIORITY[gpsd-defaults] = "5"

CONFFILES_${PN} = "${sysconfdir}/default/gpsd.refhw"

# NOTE: Explicitly not defining RPROVIDES of "virtual/gpsd-conf" to
#       avoid conflicting with the default configuration and potentially
#       changing behavior on m3ulcb/h3ulcb.
