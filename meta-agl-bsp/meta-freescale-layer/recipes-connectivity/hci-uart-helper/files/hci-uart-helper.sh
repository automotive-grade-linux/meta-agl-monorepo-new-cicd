#!/bin/sh
#
# Script to attach HCI UART devices on i.MX8MQ EVK/EVKB
#
# NOTE:
# For the most part errors are ignored and the script returns
# 0/success so BlueZ will still be started if the script is somehow
# run on a board without the expected hardware.  However, if the
# various probing succeeds and hciattach is run, the script returns
# the resulting exit code if hciattach fails.

COMPAT=/sys/firmware/devicetree/base/compatible
HCITTY=/dev/ttymxc2
PCIDEV=/sys/bus/pci/devices/0000:01:00.0

if [ ! \( -f "$COMPAT" -a -c "$HCITTY" \) ]; then
    exit 0
fi

found=false
for c in `cat $COMPAT | tr '\0' ' '`; do
    echo "c = $c"
    if echo $c | grep -q '^fsl,imx8mq-evk$'; then
        found=true
        break
    fi
done
if ! $found; then
    echo "i.MX8MQ EVK not found!"
    exit 0
fi

if [ -f "$PCIDEV/vendor" -a -f "$PCIDEV/device" ]; then
    vendor=`cat $PCIDEV/vendor`
    device=`cat $PCIDEV/device`
fi

rc=0
if [ "$vendor" = "0x14e4" -a "$device" = "0x43ec" ]; then
    # Broadcom 5436 on EVKB
    hciattach $HCITTY bcm43xx
    rc=$?
elif [ "$vendor" = "0x168c" -a "$device" = "0x003e" ]; then
    # Qualcomm (nee Atheros) 6174 on EVK
    hciattach $HCITTY qualcomm
    rc=$?
fi
exit $rc
