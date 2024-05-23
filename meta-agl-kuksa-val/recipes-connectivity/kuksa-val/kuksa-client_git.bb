SUMMARY = "Python client for KUKSA.val, the KUKSA Vehicle Abstraction Layer"
HOMEPAGE = "https://github.com/eclipse/kuksa.val"
BUGTRACKER = "https://github.com/eclipse/kuksa.val/issues"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

DEPENDS = " \
    python3-setuptools-git-versioning-native \
    python3-grpcio-tools-native \
    python3-grpcio \
"

PV = "0.5.0-a2+git${SRCPV}"

SRC_URI = "gitsm://github.com/eclipse-kuksa/kuksa-python-sdk.git;protocol=https;branch=main \
           file://0001-kuksa-client-Update-cmd2-completer-usage.patch;patchdir=.. \
           file://0002-Tweak-grpcio-tools-requirement.patch;patchdir=.. \
	   file://0003-Fix-AIO-version-of-gRPC-subscribe_target_values.patch;patchdir=.. \
"
SRCREV = "cdf8f8215043b56cad3deaacb59322926b70418a"

S = "${UNPACKDIR}/${BPN}-${PV}/kuksa-client"

inherit python_setuptools_build_meta

do_compile:prepend() {
    nativepython3 -m proto
}

RDEPENDS:${PN} += " \
    python3-cmd2 \
    python3-importlib-metadata \
    python3-pkg-resources \
    python3-pygments \
    python3-websockets \
    python3-grpcio \
    python3-grpcio-tools \
    python3-jsonpath-ng \
"

# A script for generating new certificates currently gets shipped inside
# the client module, for now add bash to RDEPENDS to quiet the QA error.
# This should probably be addressed with finer-grained packaging or some
# other change worked out with upstream.
RDEPENDS:${PN} += "bash"
