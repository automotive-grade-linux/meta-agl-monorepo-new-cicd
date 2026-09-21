SUMMARY = "AGL Users"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

inherit allarch

require agl-users.inc

ALLOW_EMPTY:${PN} = "1"
