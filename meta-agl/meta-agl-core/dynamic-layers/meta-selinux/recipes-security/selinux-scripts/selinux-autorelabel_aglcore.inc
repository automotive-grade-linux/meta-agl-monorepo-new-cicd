do_install:append() {
    # Do not force auto relabeling just from the package getting installed
    rm -f ${D}/.autorelabel
}
