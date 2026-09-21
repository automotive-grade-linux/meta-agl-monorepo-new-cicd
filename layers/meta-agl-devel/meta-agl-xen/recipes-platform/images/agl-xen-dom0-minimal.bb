DESCRIPTION = "AGL minimal Xen dom0 image"
LICENSE = "MIT"

#require recipes-core/images/core-image-minimal.bb
require recipes-platform/images/agl-image-minimal.bb

# Linux kernel option CONFIG_XEN_PCIDEV_BACKEND depends on X86
XEN_PCIBACK_MODULE = ""
XEN_PCIBACK_MODULE:x86    = "kernel-module-xen-pciback"
XEN_PCIBACK_MODULE:x86-64 = "kernel-module-xen-pciback"
XEN_ACPI_PROCESSOR_MODULE = ""
XEN_ACPI_PROCESSOR_MODULE:x86    = "kernel-module-xen-acpi-processor"
XEN_ACPI_PROCESSOR_MODULE:x86-64 = "kernel-module-xen-acpi-processor"

XEN_KERNEL_MODULES ?= " \
    kernel-module-xen-blkback \
    kernel-module-xen-gntalloc \
    kernel-module-xen-gntdev \
    kernel-module-xen-netback \
    kernel-module-xen-wdt \
    ${@bb.utils.contains('MACHINE_FEATURES', 'pci', "${XEN_PCIBACK_MODULE}", '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'acpi', '${XEN_ACPI_PROCESSOR_MODULE}', '', d)} \
"

# We do not need the kernel image in the filesystem
PACKAGE_EXCLUDE = "kernel-image-*"

IMAGE_INSTALL += " \
    boot-dom-mount \
    ${XEN_KERNEL_MODULES} \
    xen-tools \
"

# Networking for HVM-mode guests (x86/64 only) requires the tun kernel module
IMAGE_INSTALL:append:x86    = " kernel-module-tun"
IMAGE_INSTALL:append:x86-64 = " kernel-module-tun"
