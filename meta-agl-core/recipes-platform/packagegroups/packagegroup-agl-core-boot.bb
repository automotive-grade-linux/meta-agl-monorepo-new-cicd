#
# Copyright (C) 2007 OpenedHand Ltd.
#
# derived from oe-core: meta/recipes-core/packagegroups/packagegroup-core-boot.bb

SUMMARY = "Minimal boot requirements"
DESCRIPTION = "The minimal set of packages required to boot the system"
LICENSE = "MIT"

PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit packagegroup

# Disto can override
VIRTUAL-RUNTIME_rngd ?= "rng-tools"

RDEPENDS:${PN} = "\
    packagegroup-core-boot \
    ${VIRTUAL-RUNTIME_rngd} \
"

RRECOMMENDS:${PN} = "\
    tzdata \
"

