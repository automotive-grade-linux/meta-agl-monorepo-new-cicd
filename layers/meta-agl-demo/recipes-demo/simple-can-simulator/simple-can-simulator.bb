DESCRIPTION = "Simulator that generates CAN messages of a driving car"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${UNPACKDIR}/simple_can_simulator.py;beginline=2;endline=22;md5=9bb9c582301261d21460d2bc5bb8c225"

SRC_URI = "file://simple_can_simulator.py"

S = "${UNPACKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${UNPACKDIR}/simple_can_simulator.py ${D}${sbindir}
}

RDEPENDS:${PN} = "python3"
