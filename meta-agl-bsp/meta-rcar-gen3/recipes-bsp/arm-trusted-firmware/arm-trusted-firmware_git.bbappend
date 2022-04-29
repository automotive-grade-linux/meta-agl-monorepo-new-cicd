FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://0001-Boot-Normal-World-in-EL2.patch \
"

do_ipl_opt_deploy:prepend () {
    # Work around bug in BSP recipe, it can fail if nothing else has
    # happened to run first and create the directory.
    install -d ${DEPLOY_DIR_IMAGE}
}
