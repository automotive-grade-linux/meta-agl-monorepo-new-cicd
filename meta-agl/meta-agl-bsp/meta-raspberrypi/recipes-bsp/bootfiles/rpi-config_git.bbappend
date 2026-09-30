DISABLE_OVERSCAN = "1"

# ENABLE_UART is set globally

# GPU_MEM is set globally
# Note that the VC4 GPU driver on > Pi4 does not use the
# GPU_MEM memory for 3D, it uses regular kernel memory
# allocation.

# VC4 overlay is now board specific
VC4DTBO:raspberrypi4 = "vc4-kms-v3d-pi4,cma-256"
VC4DTBO:raspberrypi5 = "vc4-kms-v3d-pi5,cma-256"

do_deploy:append:rpi() {
    # Populate optional CAN HAT configuration
    cat <<EOF >>${DEPLOYDIR}/bootfiles/config.txt

# Enable onboard audio
dtparam=audio=on

# Support two displays
max_framebuffers=2

# RPi Touchscreen
#dtoverlay=rpi-ft5406

# Generic MCP251[78] CAN FD HAT
#dtparam=spi=on
#dtoverlay=spi1-3cs
#dtoverlay=mcp251xfd,spi0-0,interrupt=25
#dtoverlay=mcp251xfd,spi1-0,interrupt=24

# Seeed CAN FD HATs
# v1 with MCP2517
#dtoverlay=seeed-can-fd-hat-v1
# v2 with MCP2518 plus RTC
#dtoverlay=seeed-can-fd-hat-v2
EOF
}
