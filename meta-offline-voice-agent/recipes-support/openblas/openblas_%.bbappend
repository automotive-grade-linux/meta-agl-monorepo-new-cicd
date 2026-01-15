# Required to build python3-scipy
PACKAGECONFIG = "cblas affinity dynarch"
PACKAGECONFIG:remove = "openmp"
PACKAGECONFIG:append:class-target = " lapacke lapack"

# Skip building tests for native
do_compile:class-native() {
   oe_runmake HOSTCC="${BUILD_CC}" \
        CC="${TARGET_PREFIX}gcc ${TOOLCHAIN_OPTIONS}" \
        PREFIX=${exec_prefix} \
        CROSS=1 \
        CROSS_SUFFIX=${HOST_PREFIX} \
        NO_STATIC=1 \
        NO_AFFINITY=1 \
        USE_OPENMP=0 \
        ${@bb.utils.contains('PACKAGECONFIG', 'lapack', 'NO_LAPACK=0', 'NO_LAPACK=1', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'cblas', 'NO_CBLAS=0', 'NO_CBLAS=1', d)} \
        ${@bb.utils.contains('PACKAGECONFIG', 'dynarch', 'DYNAMIC_ARCH=1', 'DYNAMIC_ARCH=0', d)} \
        BINARY='${@map_bits(d.getVar('TARGET_ARCH', True), d)}' \
        TARGET='${@map_arch(d.getVar('TARGET_ARCH', True), d)}' \
        libs shared
}
