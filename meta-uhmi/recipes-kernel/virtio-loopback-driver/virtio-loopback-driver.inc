PV = "0.0+git${SRCPV}"
SRCREV = "3d91d146002123bfbdee76d3ed0f24013860b19a"
BRANCH ?= "main"
SRC_URI = " \
   git://github.com/unified-hmi/virtio-loopback-driver.git;protocol=https;branch=${BRANCH} \
"
