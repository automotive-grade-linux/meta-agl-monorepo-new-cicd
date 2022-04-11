# Remove GBM support from Mesa
# TI Platforms provides own GBM library

PACKAGES:remove = "libgbm"
PACKAGES:remove = "libgbm-dev"

EXTRA_OECONF:remove = "--enable-gbm"
