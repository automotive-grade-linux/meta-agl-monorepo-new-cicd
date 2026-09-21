# Boot Normal World in EL2: this define configures ATF (SPSR register) to boot
# BL33 in EL2.
EXTRA_OEMAKE += " RCAR_BL33_EXECUTION_EL=1"

do_ipl_opt_deploy:prepend () {
    # Work around bug in BSP recipe, it can fail if nothing else has
    # happened to run first and create the directory.
    install -d ${DEPLOY_DIR_IMAGE}
}
