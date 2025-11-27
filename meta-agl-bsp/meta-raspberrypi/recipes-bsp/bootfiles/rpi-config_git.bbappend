DISABLE_OVERSCAN = "1"
TOTAL_BOARD_MEM = "3072"

do_deploy:append:raspberrypi4() {
    # Populate optional CAN HAT configuration
    cat <<EOF >>${DEPLOYDIR}/bootfiles/config.txt

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

    # Handle setup with armstub file
    if [ -n "${ARMSTUB}" ]; then
        cat <<EOF >>${DEPLOYDIR}/bootfiles/config.txt

# ARM stub configuration
armstub=${ARMSTUB}
EOF
        case "${ARMSTUB}" in
            *-gic.bin)
                echo  "enable_gic=1" >> ${DEPLOYDIR}/bootfiles/config.txt
                ;;
        esac
    fi
}

do_deploy:append() {
    if [ "${ENABLE_CMA}" = "1" ] && [ -n "${CMA_LWM}" ]; then
        sed -i '/#cma_lwm/ c\cma_lwm=${CMA_LWM}' ${DEPLOYDIR}/bootfiles/config.txt
    fi

    if [ "${ENABLE_CMA}" = "1" ] && [ -n "${CMA_HWM}" ]; then
        sed -i '/#cma_hwm/ c\cma_hwm=${CMA_HWM}' ${DEPLOYDIR}/bootfiles/config.txt
    fi

    cat <<EOF >>${DEPLOYDIR}/bootfiles/config.txt

avoid_warnings=2
mask_gpu_interrupt0=0x400
dtoverlay=vc4-kms-v3d-overlay,cma-256
dtoverlay=rpi-ft5406-overlay
dtparam=audio=on
EOF
}

do_deploy:append:raspberrypi4() {
    cat <<EOF >>${DEPLOYDIR}/bootfiles/config.txt

[pi4]
max_framebuffers=2
EOF
}
