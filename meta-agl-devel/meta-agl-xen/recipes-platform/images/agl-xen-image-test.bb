DESCRIPTION = "AGL test partitioned Xen image"
LICENSE = "MIT"

inherit agl-xen-image

DOM0_IMAGE ?= "agl-xen-dom0-test"
DOMD_IMAGE ?= ""

DOMU_IMAGES ?= "agl-xen-domu:agl-image-minimal"
