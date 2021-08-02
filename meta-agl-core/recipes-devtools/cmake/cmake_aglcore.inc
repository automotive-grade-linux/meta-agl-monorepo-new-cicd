FILESEXTRAPATHS:append := ":${THISDIR}/files"

SRC_URI:append:class-nativesdk = " \
    file://environment.d-cmake-agl.sh \
"

do_install:append:class-nativesdk() {
    install -m 644 ${WORKDIR}/environment.d-cmake-agl.sh ${D}${SDKPATHNATIVE}/environment-setup.d/cmake-agl.sh
}
