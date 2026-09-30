# setup for PRSERV and HASHSERV
AGL_HOST_PRSERV ?= "prserv.automotivelinux.org"
AGL_HOST_HASHSERV ?= "prserv.automotivelinux.org"

# r/o hashserv
BB_HASHSERVE = "auto"
BB_HASHSERVE_UPSTREAM = "${AGL_HOST_HASHSERV}:8888"
BB_SIGNATURE_HANDLER = "OEEquivHash"

# r/o prserv
PRSERV_HOST = "${AGL_HOST_PRSERV}:8686"

IMAGE_INSTALL:append = " curl"
