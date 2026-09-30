# Default configuration is effectively for Xen dom0 building

AGL_FEATURES:append = " agl-xen-dom0"

# Make sure ext4 image is available for partitioned image build
AGL_DEFAULT_IMAGE_FSTYPES:append = " ext4"

# Pull in any machine-specific configuration
include mc-conf-default-${MACHINE}.inc
