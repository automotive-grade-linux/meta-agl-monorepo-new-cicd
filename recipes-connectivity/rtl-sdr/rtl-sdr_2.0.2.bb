SUMMARY = "Turns a Realtek RTL2832U-based DVB dongle into a SDR receiver"
DESCRIPTION = "DVB-T dongles based on the Realtek RTL2832U chipset can be used as Software Digital Radio adapters, since the chip allows transferring raw I/Q samples to the host, which is really used for DAB/DAB+/FM demodulation."
HOMEPAGE = "http://sdr.osmocom.org/trac/wiki/rtl-sdr"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe \
                    file://src/librtlsdr.c;endline=18;md5=1b05599c3ebd4d74857a0a7c45f3d4ef"

DEPENDS = "libusb1"

SRC_URI = "git://git.osmocom.org/rtl-sdr;branch=master \
           file://0001-Fix-version.patch \
"
SRCREV = "619ac3186ea0ffc092615e1f59f7397e5e6f668c"

inherit cmake pkgconfig

PACKAGECONFIG ??= "detach-kernel-driver usbfs-zero-copy"

PACKAGECONFIG[detach-kernel-driver] = "-DDETACH_KERNEL_DRIVER=ON,-DDETACH_KERNEL_DRIVER=OFF"
PACKAGECONFIG[install-udev-rules] = "-DINSTALL_UDEV_RULES=ON,-DINSTALL_UDEV_RULES=OFF"
PACKAGECONFIG[usbfs-zero-copy] = "-DENABLE_ZEROCOPY=ON,-DENABLE_ZEROCOPY=OFF"

do_install:append() {
    # Fix up paths in generated cmake files
    sed -i 's|${RECIPE_SYSROOT}${prefix}|${_IMPORT_PREFIX}|g' ${D}${libdir}/cmake/rtlsdr/rtlsdrTargets.cmake
}
