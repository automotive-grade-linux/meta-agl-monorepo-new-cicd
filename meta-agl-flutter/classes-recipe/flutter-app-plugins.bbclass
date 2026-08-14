#
# flutter-app class wrapper to enable building native plugins
# during Flutter app build
#

TOOLCHAIN = "clang"
# Required to make dart happy
DEPENDS:append = " lld-native"

# Needed until meta-flutter is updated, flutter-app.bbclass does this already
# in newer versions
include conf/include/gn-utils.inc
FLUTTER_BUILD_ARGS:append = " --target-platform linux-${@gn_target_arch_name(d)}"

# Mask out build path in compiled plugin code
DEBUG_PREFIX_MAP += "-ffile-prefix-map=${PUB_CACHE}/hosted/pub.dev=${TARGET_DBGSRC_DIR}"

# Assume cmake and pkgconfig will be used for non-trivial native plugins
inherit cmake pkgconfig

# Skip cmake do_configure
do_configure[noexec] = "1"
do_compile[prefuncs] += "flutter_native_cmake_setup flutter_native_path_setup"

flutter_native_cmake_setup() {
    # Create cmake wrapper to insert OE environment options
    cat > ${WORKDIR}/cmake <<EOF
#!/bin/sh
export PKG_CONFIG_PATH="${PKG_CONFIG_PATH}"
export PKG_CONFIG_LIBDIR="${PKG_CONFIG_LIBDIR}"
export PKG_CONFIG_SYSROOT_DIR="${PKG_CONFIG_SYSROOT_DIR}"
export PKG_CONFIG_DISABLE_UNINSTALLED="${PKG_CONFIG_DISABLE_UNINSTALLED}"
export PKG_CONFIG_SYSTEM_LIBRARY_PATH="${PKG_CONFIG_SYSTEM_LIBRARY_PATH}"
export PKG_CONFIG_SYSTEM_INCLUDE_PATH="${PKG_CONFIG_SYSTEM_INCLUDE_PATH}"

configuring="true"
for arg in "\$@"; do
    if [ "\${arg}" = "--build" ]; then
        configuring="false"
    fi
done
CMAKE_ARGS=""
if [ "\${configuring}" = "true" ]; then
    CMAKE_ARGS="${OECMAKE_ARGS}"
fi
exec ${RECIPE_SYSROOT_NATIVE}${bindir}/cmake \${CMAKE_ARGS} "\$@"
EOF
    chmod +x ${WORKDIR}/cmake
}

python flutter_native_path_setup() {
    # Ensure cmake wrapper is found
    path = d.getVar('PATH')
    workdir = d.getVar('WORKDIR')
    d.setVar('PATH', workdir + ':' + path)
}

do_install:append() {
    if [ -d ${S}/${FLUTTER_APPLICATION_PATH}/build/native_assets/linux ]; then
        cp -r ${S}/${FLUTTER_APPLICATION_PATH}/build/native_assets/linux/* \
            ${D}${FLUTTER_INSTALL_DIR}/${FLUTTER_SDK_VERSION}/${FLUTTER_RUNTIME_MODE}/lib/
    fi
}

# Ensure do_compile has a clean slate when it runs
do_compile[cleandirs] = "${S}/.dart_tool"

# Quiet QA warnings about debug libraries under /usr/share/flutter/.../lib/.debug
INSANE_SKIP:${PN}-dbg += " libdir"

inherit flutter-app
