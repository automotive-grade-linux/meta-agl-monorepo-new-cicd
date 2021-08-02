require af-binder_${PV}.inc

DEPENDS = "file json-c libmicrohttpd systemd util-linux openssl cynara"

inherit cmake pkgconfig

EXTRA_OECMAKE:append:class-target = "\
	-DUNITDIR_SYSTEM=${systemd_system_unitdir} \
"

EXTRA_OECMAKE:append:agl-devel = " \
	-DAGL_DEVEL=ON \
	-DINCLUDE_MONITORING=ON \
	-DINCLUDE_SUPERVISOR=ON -DAFS_SUPERVISION_SOCKET=/run/platform/supervisor \
"

pkg_postinst:${PN}() {
	mkdir -p "$D${libdir}/afb"
}

do_install:append:agl-devel:class-target() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d -m 0755 ${D}${systemd_system_unitdir}/multi-user.target.wants
        ln -s ../afm-api-supervisor.service ${D}${systemd_system_unitdir}/multi-user.target.wants/afm-api-supervisor.service
    fi
}

#############################################
# main package
#############################################

FILES:${PN}:append:agl-devel = " ${libdir}/afb/monitoring ${systemd_system_unitdir}"

RDEPENDS:${PN}-dev += "libafbwsc-dev"

#############################################
# intrinsic binding packages
#############################################
PACKAGES =+ "${PN}-intrinsic-bindings"
ALLOW_EMPTY:${PN}-intrinsic-bindings = "1"

PACKAGES_DYNAMIC = "${PN}-binding-*"

python populate_packages:prepend () {
    afb_libdir = d.expand('${libdir}/afb')
    postinst = d.getVar('binding_postinst', True)
    pkgs = []

    pkgs += do_split_packages(d, afb_libdir, '(.*)-api\.so$', d.expand('${PN}-binding-%s'), 'AFB binding for %s', postinst=postinst, extra_depends=d.expand('${PN}'))
    pkgs += do_split_packages(d, afb_libdir, '(.*(?!-api))\.so$', d.expand('${PN}-binding-%s'), 'AFB binding for %s', postinst=postinst, extra_depends=d.expand('${PN}'))

    d.setVar('RDEPENDS:' + d.getVar('PN', True) + '-intrinsic-bindings', ' '.join(pkgs))
}

#############################################
# tool package
#############################################
PACKAGES =+ "${PN}-tools"

FILES:${PN}-tools = "\
	${bindir}/afb-client-demo \
"

#############################################
# setup libafbwsc package
#############################################
PACKAGES =+ "libafbwsc libafbwsc-dev"

FILES:libafbwsc = "\
	${libdir}/libafbwsc.so.* \
"
FILES:libafbwsc-dev = "\
	${includedir}/afb/afb-wsj1.h \
	${includedir}/afb/afb-ws-client.h \
	${libdir}/libafbwsc.so \
	${libdir}/pkgconfig/libafbwsc.pc \
"

#############################################
# devtool package
#############################################
PACKAGES =+ "${PN}-devtools"

FILES:${PN}-devtools = "\
	${bindir}/afb-exprefs \
	${bindir}/afb-json2c \
	${bindir}/afb-genskel \
"

#############################################
# supervisor package
#############################################
PACKAGES:append:agl-devel = " ${PN}-supervisor "

FILES:${PN}-supervisor:agl-devel = "\
	${bindir}/afs-supervisor \
        ${systemd_system_unitdir} \
"

#############################################
# setup sample packages
#############################################
PACKAGES =+ "${PN}-samples"

FILES:${PN}-samples = "\
	${datadir}/af-binder \
"

#############################################
# meta package
#############################################
PACKAGES =+ "${PN}-meta"
ALLOW_EMPTY:${PN}-meta = "1"

RDEPENDS:${PN}-meta += "${PN} ${PN}-tools libafbwsc ${PN}-intrinsic-bindings"
RDEPENDS:${PN}-meta:append:agl-devel = " ${PN}-supervisor "

