SUMMARY = "A digital instrument cluster written in Rust and Slint, built for Automotive Grade Linux."
HOMEPAGE = "https://github.com/signal-slot/agl-slint-cluster"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=1cd9f3535d038f9d1bb320918e6474b1"

inherit slint_common cargo_bin pkgconfig features_check systemd

SRC_URI = " \
    git://github.com/signal-slot/agl-slint-cluster.git;protocol=https;branch=main \
    file://agl-slint-cluster.service \
"
SRCREV = "37e37108d94a577e9f9beb85fe4c1901d0cc9507"

REQUIRED_DISTRO_FEATURES:append = ""
REQUIRED_DISTRO_FEATURES:append:class-target = "opengl"

DEPENDS:append:class-target = " \
    fontconfig libxkbcommon libdrm virtual/egl virtual/libgbm virtual/libgles2 \
    clang-cross-${TARGET_ARCH} ca-certificates-native curl-native ninja-native \
    seatd udev libinput \
    "

CARGO_MANIFEST_PATH = "${S}/rust/Cargo.toml"
CARGO_DISABLE_BITBAKE_VENDORING = "1"
CARGO_FEATURES = "slint/backend-linuxkms slint/renderer-skia"

RUSTFLAGS += "--remap-path-prefix=${WORKDIR}=${TARGET_DBGSRC_DIR}"

python () {
    if 'scarthgap' in (d.getVar('LAYERSERIES_CORENAMES') or '').split():
        d.setVar('S', '${WORKDIR}/git')
}

PV = "0.1"

SYSTEMD_SERVICE:${PN} = "agl-slint-cluster.service"

do_configure[network] = "1"
do_compile[network] = "1"

do_compile:prepend() {
    CURL_CA_BUNDLE=${STAGING_DIR_NATIVE}/etc/ssl/certs/ca-certificates.crt
    export CURL_CA_BUNDLE

    # Skia + LTO is very RAM-hungry; keep LTO off (as slint-demos does). The job
    # count is bounded globally via CARGO_BUILD_JOBS (see common.sh).
    export CARGO_PROFILE_RELEASE_LTO=false
}

do_install:append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${UNPACKDIR}/agl-slint-cluster.service ${D}${systemd_unitdir}/system/
}
