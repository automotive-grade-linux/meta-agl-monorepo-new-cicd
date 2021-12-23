# Build updatable image. Only takes effect when sota.bbclass is inherited
#DISTRO_FEATURES:append = " sota"

# Root device
ROOT_VM = "root=PARTUUID=${DISK_SIGNATURE}-02"

# Use our own wks file
WKS_FILE="directdisk.wks.in"

# Use pixman as software renderer to avoid high cpu consumption by GL-renderer
WESTON_USE_PIXMAN = "1"

# Over-ride setting in oe-core's qemuboot.bbclass
QB_MEM:qemuarm64 = "-m 2048"

# Changes to make runqemu work out-of-the-box
QB_AUDIO_DRV:qemuarm64 := "alsa"
QB_MACHINE:qemuarm64 := "-machine virt -smp 4"
QB_OPT_APPEND:qemuarm64 := " -show-cursor -device VGA,vgamem_mb=64,edid=on -device qemu-xhci -device usb-tablet -device usb-kbd -object rng-random,filename=/dev/urandom,id=rng0 -device virtio-rng-pci,rng=rng0"
QB_KERNEL_CMDLINE_APPEND:qemuarm64 := "console=ttyAMA0,115200n8 verbose"