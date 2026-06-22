# Enable here for now due to meta-rcar-gen3 bbappends being masked
# out, can be removed once gstreamer 1.22.x is properly supported
# there.
EXTRA_OECONF += "--enable-kms"
PACKAGECONFIG:append = " kms"
