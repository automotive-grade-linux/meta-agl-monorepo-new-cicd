require agl_qemu.inc

# Root device
ROOT_VM = "root=PARTUUID=${DISK_SIGNATURE}-02"

# Use pixman as software renderer to avoid high cpu consumption by GL-renderer
WESTON_USE_PIXMAN = "1"

# Over-ride setting in oe-core's qemuboot.bbclass
QB_MEM:qemuarm = "-m 2048"

# Changes to make runqemu work out-of-the-box
QB_AUDIO_DRV:qemuarm := "alsa"
QB_MACHINE:qemuarm := "-machine virt-2.11 -smp 4"
QB_OPT_APPEND:qemuarm := "-device VGA,vgamem_mb=64,edid=on -device qemu-xhci -device usb-tablet -device usb-kbd"
QB_KERNEL_CMDLINE_APPEND:qemuarm := "console=ttyAMA0 verbose vmalloc=256M"
