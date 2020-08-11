# DEFAULTTUNE overrides for AGL on riscv64

# We should not need the tuning include below as the BSP should include the right set already.
# A double inclusion would produce a warning. This include line is just for reference
# include conf/machine/include/arm/arch-armv8.inc

DEFAULTTUNE = "riscv64"
