# For Xen
SRC_URI:append =" \
    ${@bb.utils.contains('AGL_XEN_WANTED','1','file://0002-Disable-DMA-in-sdhci-driver.patch','',d)} \
"
