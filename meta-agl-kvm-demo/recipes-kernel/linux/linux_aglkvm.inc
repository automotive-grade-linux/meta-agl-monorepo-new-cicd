FILESEXTRAPATHS:prepend := "${THISDIR}/linux:"

# Add KVM demo specific guest device configuration.
# Most of these are for devices that may be getting passed through,
# in an ideal world they would be replaced by virtio devices.
AGL_KCONFIG_FRAGMENTS_DEMO:virtio-all:append = " \
    kvm-demo-can-devices.cfg \
    kvm-demo-usb.cfg \
    usbaudio.cfg \
"
