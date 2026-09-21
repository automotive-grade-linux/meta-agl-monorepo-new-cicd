SUMMARY = "Facial Expression Recognition with a deep neural network"
HOMEPAGE = "https://github.com/justinshenk/fer"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=09736b80b8d94e82838cbe0c1211a141"

SRC_URI[sha256sum] = "37f263b38e830899fb5237cad74eb4b0558adeb67f44d881a40b8573d9716d7b"

inherit pypi python_setuptools_build_meta

# Core dependencies
RDEPENDS:${PN} += " \
    python3-core \
    python3-matplotlib \
    python3-opencv \
    python3-pandas \
    python3-requests \
    python3-pillow \
    python3-tqdm \
    python3-numpy \
"
