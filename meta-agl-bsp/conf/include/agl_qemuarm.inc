# Build updatable image. Only takes effect when sota.bbclass is inherited
#DISTRO_FEATURES_append = " sota"

# Root device
ROOT_VM = "root=PARTUUID=${DISK_SIGNATURE}-02"

# Use our own wks file
WKS_FILE="directdisk.wks.in"

# Over-ride setting in oe-core's qemuboot.bbclass
QB_MEM_qemuarm = "-m 2048"

# Changes to make runqemu work out-of-the-box
QB_AUDIO_DRV_qemuarm := "alsa"
QB_MACHINE_qemuarm := "-machine virt-2.11 -smp 4"
QB_OPT_APPEND_qemuarm := "-show-cursor -device VGA,vgamem_mb=64,edid=on -device qemu-xhci -device usb-tablet -device usb-kbd -object rng-random,filename=/dev/urandom,id=rng0 -device virtio-rng-pci,rng=rng0"
QB_KERNEL_CMDLINE_APPEND_qemuarm := "console=ttyAMA0 verbose vmalloc=256M"
