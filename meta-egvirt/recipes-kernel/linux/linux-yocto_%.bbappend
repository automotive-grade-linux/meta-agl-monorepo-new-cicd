FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# virtio BT
SRC_URI += " \
    file://virtio_bt.cfg \
"

# virtio loopback
SRC_URI += " \
    file://virtio_loopback.cfg \
"

# virtio can
SRC_URI += " \
    file://virtio_can.cfg \
"

# virtio input
SRC_URI += " \
    file://virtio_input.cfg \
"

# virtio sound
SRC_URI += " \
    file://virtio_sound.cfg \
"
