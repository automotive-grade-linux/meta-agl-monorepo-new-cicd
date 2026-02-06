SUMMARY = "COVESA Vehicle Signal Specification tooling."
HOMEPAGE = "https://github.com/COVESA/vss-tools"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"

SRC_URI = "git://github.com/COVESA/vss-tools.git;protocol=https;branch=release/6.0"
SRCREV = "db47df2f454939bb3f2cb6f80e9951083f81fee5"

S = "${WORKDIR}/git"

inherit python_hatchling

RDEPENDS:${PN} += " \
    python3-core \
    python3-anytree \
    python3-click \
    python3-graphql-core \
    python3-jsonschema \
    python3-pandas \
    python3-pydantic \
    python3-pyyaml \
    python3-rdflib \
    python3-rich-click \
"

BBCLASSEXTEND += "native nativesdk"
