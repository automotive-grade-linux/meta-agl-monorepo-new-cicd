PV = "0.0+git${SRCPV}"
SRCREV = "8f18b991e2d560b09f69cbcc90ef11be85865349"
BRANCH ?= "main"
SRC_URI = " \
    git://github.com/unified-hmi/remote-virtio-gpu.git;protocol=https;branch=${BRANCH} \
"
