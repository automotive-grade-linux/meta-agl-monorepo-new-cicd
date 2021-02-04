#!/bin/sh
#
# Script to detect AGL Reference Hardware and switch gpsd
# configuration as necessary.
#
# NOTE:
# For the most part errors are ignored and the script returns
# 0/success so gpsd still be started if the script is somehow
# run on a board without the expected hardware.

COMPAT=/sys/firmware/devicetree/base/compatible
GPSTTY=/dev/ttySC3

#if [ ! \( -f "$COMPAT" -a -c "$GPSTTY" -a -f /etc/default/gpsd.refhw \) ]; then
if [ ! \( -f "$COMPAT" -a -f /etc/default/gpsd.refhw \) ]; then
    exit 0
fi

found=false
for c in `cat $COMPAT | tr '\0' ' '`; do
    if echo $c | grep -q '^agl,refhw-h3$'; then
        found=true
        break
    fi
done

if $found; then
    if [ ! -c "$GPSTTY" ]; then
        exit 0
    fi
    update-alternatives --install /etc/default/gpsd gpsd-defaults /etc/default/gpsd.refhw 20
else
    update-alternatives --install /etc/default/gpsd gpsd-defaults /etc/default/gpsd.refhw 5
fi
exit 0
