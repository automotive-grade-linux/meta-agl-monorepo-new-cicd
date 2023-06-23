DEPENDS += " chromium "

EXTRA_OECMAKE = "\
    -DCMAKE_BUILD_TYPE=Release \
    -DCMAKE_INSTALL_PREFIX=${prefix} \
    -DPLATFORM_NAME=${@'${DISTRO}'.upper().replace('-', '_')} \
    -DCHROMIUM_SRC_DIR=${STAGING_INCDIR}/chromium"

do_install:append() {
    install -v -D -m 644 ${WORKDIR}/WebAppMgr.env ${D}${sysconfdir}/default/WebAppMgr.env
    sed -i -e 's#@WAM_EXE_DIR@#${bindir}#g' ${D}${systemd_system_unitdir}/WebAppMgr.service
}
