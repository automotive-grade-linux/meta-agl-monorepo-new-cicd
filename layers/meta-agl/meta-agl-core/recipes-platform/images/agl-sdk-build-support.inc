# Common SDK buld support
# This file should be included by image recipe

# If agl-devel set to feature in aglsetup, sanitizer support is enabled.
AGLDEV_SANITIZERS = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'agl-devel', 'gcc-sanitizers', '', d)} \
"

# gcc-sanitizers is currently (scarthgap) not supported on RISC-V
AGLDEV_SANITIZERS:riscv64 = ""

IMAGE_INSTALL:append = " ${AGLDEV_SANITIZERS}"

# Enable Rust
SDK_TOOLCHAIN_LANGS += "rust"

# For automake
TOOLCHAIN_HOST_TASK += "nativesdk-perl-modules"
