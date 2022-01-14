# This recipe was written by Carlos Rafael Giani <crg7475@mailbox.org>
# for meta-oe
#
# The intention is to keep this synced with meta-oe and remove it when we
# can depend on meta-oe to provide this recipe for us
#
# AGL-specific overrides and configuration should go in the .bbappend file
#
SUMMARY = "Multimedia processing server for Linux"
DESCRIPTION = "Linux server for handling and routing audio and video streams between applications and multimedia I/O devices"
HOMEPAGE = "https://pipewire.org/"
BUGTRACKER  = "https://gitlab.freedesktop.org/pipewire/pipewire/issues"
LICENSE = "MIT & GPL-2.0-only & LGPL-2.1-or-later"

LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=2158739e172e58dc9ab1bdd2d6ec9c72 \
    file://COPYING;md5=97be96ca4fab23e9657ffa590b931c1a \
"
SECTION = "multimedia"

DEPENDS = "dbus"

# v0.3.43
SRCREV = "07724b7aefa8a23a016727b53f4e426ecd63d248"
SRC_URI = "git://gitlab.freedesktop.org/pipewire/pipewire.git;branch=master;protocol=https"

S = "${WORKDIR}/git"

inherit meson pkgconfig systemd manpages gettext useradd

USERADD_PACKAGES = "${PN}"

GROUPADD_PARAM:${PN} = "--system pipewire"

USERADD_PARAM:${PN} = "--system --home / --no-create-home \
                       --comment 'PipeWire multimedia daemon' \
                       --gid pipewire --groups audio,video \
                       pipewire"

# For "EVL", look up https://evlproject.org/ . It involves
# a specially prepared kernel, and is currently unavailable
# in Yocto.
#
# FFmpeg and Vulkan aren't really supported - at the current
# stage (version 0.3.22), these are just experiments, not
# actual features.
#
# libcamera support currently does not build successfully.
#
# systemd user service files are disabled because per-user
# PipeWire instances aren't really something that makes
# much sense in an embedded environment. A system-wide
# instance does.
#
# manpage generation requires xmltoman, which is not available.
#
# Dont build any session managers along with pipewire
# wireplumber is the session manger used in AGL and it will
# be build in a different recipe
#
EXTRA_OEMESON += " \
    -Daudiotestsrc=enabled \
    -Devl=disabled \
    -Dsystemd-user-service=disabled \
    -Dtests=disabled \
    -Dudevrulesdir=${nonarch_base_libdir}/udev/rules.d/ \
    -Dvideotestsrc=enabled \
    -Dffmpeg=disabled \
    -Dvulkan=disabled \
    -Dlibcamera=disabled \
    -Dman=disabled \
    -Dsession-managers=[] \
"

PACKAGECONFIG ??= "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'bluetooth', 'bluez', '', d)} \
    ${@bb.utils.filter('DISTRO_FEATURES', 'alsa systemd', d)} \
    gstreamer jack v4l2 \
"

# "jack" and "pipewire-jack" packageconfigs cannot be both enabled,
# since "jack" imports libjack, and "pipewire-jack" generates
# libjack.so* files, thus colliding with the libpack package. This
# is why these two are marked in their respective packageconfigs
# as being in conflict.

PACKAGECONFIG[alsa] = "-Dalsa=enabled,-Dalsa=disabled,alsa-lib udev"
PACKAGECONFIG[bluez] = "-Dbluez5=enabled,-Dbluez5=disabled,bluez5 sbc"
PACKAGECONFIG[docs] = "-Ddocs=enabled,-Ddocs=disabled,doxygen-native"
PACKAGECONFIG[gstreamer] = "-Dgstreamer=enabled,-Dgstreamer=disabled,glib-2.0 gstreamer1.0 gstreamer1.0-plugins-base"
PACKAGECONFIG[jack] = "-Djack=enabled,-Djack=disabled,jack,,,pipewire-jack"
PACKAGECONFIG[sdl2] = "-Dsdl2=enabled,-Dsdl2=disabled,virtual/libsdl2"
PACKAGECONFIG[sndfile] = "-Dsndfile=enabled,-Dsndfile=disabled,libsndfile1"
PACKAGECONFIG[systemd] = "-Dsystemd=enabled -Dsystemd-system-service=enabled ,-Dsystemd=disabled -Dsystemd-system-service=disabled,systemd"
PACKAGECONFIG[v4l2] = "-Dv4l2=enabled,-Dv4l2=disabled,udev"
PACKAGECONFIG[pipewire-alsa] = "-Dpipewire-alsa=enabled,-Dpipewire-alsa=disabled,alsa-lib"
PACKAGECONFIG[pipewire-jack] = "-Dpipewire-jack=enabled -Dlibjack-path=${libdir}/${PW_MODULE_SUBDIR}/jack,-Dpipewire-jack=disabled,jack,,,jack"
PACKAGECONFIG[pipewire-v4l2] = "-Dpipewire-v4l2=enabled -Dpipewire-v4l2=${libdir}/${PW_MODULE_SUBDIR}/v4l2,-Dpipewire-v4l2=disabled,v4l2"

PACKAGESPLITFUNCS:prepend = " split_dynamic_packages "
PACKAGESPLITFUNCS:append = " set_dynamic_metapkg_rdepends "
PACKAGESPLITFUNCS:append = " fixup_dynamic_pkg_licenses "

SPA_SUBDIR = "spa-0.2"
PW_MODULE_SUBDIR = "pipewire-0.3"

remove_unused_installed_files() {
    # jack.conf is used by pipewire-jack (not the JACK SPA plugin).
    # Remove it if pipewire-jack is not built to avoid creating the
    # pipewire-jack package.
    if ${@bb.utils.contains('PACKAGECONFIG', 'pipewire-jack', 'false', 'true', d)}; then
        rm -f "${D}${datadir}/pipewire/jack.conf"
    fi
}

do_install[postfuncs] += "remove_unused_installed_files"

python fixup_dynamic_pkg_licenses () {
    #dynamic packages inherit currently whatever is specified in LICENSE (thus multiple)
    packages = (d.getVar('PACKAGES') or "").split()

    for pkg in packages:
        # we manually assign the LICENSES here to cover all packages (even dynamically created ones)
        d.setVar("LICENSE:" + pkg ,"MIT")

        # next handle special cases
        # ==> LICENSE:${PN}-spa-plugins-alsa = "LGPL-2.1-or-later"
        if "pipewire-spa-plugins-alsa" in pkg:
            d.setVar("LICENSE:pipewire-spa-plugins-alsa", "LGPL-2.1-or-later")
        # ==> LICENSE:${PN}-alsa-card-profile = "LGPL-2.1-or-later"
        if "pipewire-alsa-card-profile" in pkg:
            d.setVar("LICENSE:pipewire-alsa-card-profile", "LGPL-2.1-or-later")
        # ==> LICENSE:${PN}-jack = "GPL-2.0-only"
        if "pipewire-jack" in pkg:
            d.setVar("LICENSE:pipewire-jack", "GPL-2.0-only")
}

python split_dynamic_packages () {
    # Create packages for each SPA plugin. These plugins are located
    # in individual subdirectories, so a recursive search is needed.
    spa_libdir = d.expand('${libdir}/${SPA_SUBDIR}')
    do_split_packages(d, spa_libdir, r'^libspa-(.*)\.so$', d.expand('${PN}-spa-plugins-%s'), 'PipeWire SPA plugin for %s', extra_depends='', recursive=True)

    # Create packages for each PipeWire module.
    pw_module_libdir = d.expand('${libdir}/${PW_MODULE_SUBDIR}')
    do_split_packages(d, pw_module_libdir, r'^libpipewire-module-(.*)\.so$', d.expand('${PN}-modules-%s'), 'PipeWire %s module', extra_depends='', recursive=False)
}

python set_dynamic_metapkg_rdepends () {
    import os
    import oe.utils

    # Go through all generated SPA plugin and PipeWire module packages
    # (excluding the main package and the -meta package itself) and
    # add them to the -meta package as RDEPENDS.

    base_pn = d.getVar('PN')

    spa_pn = base_pn + '-spa-plugins'
    spa_metapkg =  spa_pn + '-meta'

    pw_module_pn = base_pn + '-modules'
    pw_module_metapkg =  pw_module_pn + '-meta'

    d.setVar('ALLOW_EMPTY:' + spa_metapkg, "1")
    d.setVar('FILES:' + spa_metapkg, "")

    d.setVar('ALLOW_EMPTY:' + pw_module_metapkg, "1")
    d.setVar('FILES:' + pw_module_metapkg, "")

    blacklist = [ spa_pn, spa_metapkg, pw_module_pn, pw_module_metapkg ]
    spa_metapkg_rdepends = []
    pw_module_metapkg_rdepends = []
    pkgdest = d.getVar('PKGDEST')

    for pkg in oe.utils.packages_filter_out_system(d):
        if pkg in blacklist:
            continue

        is_spa_pkg = pkg.startswith(spa_pn)
        is_pw_module_pkg = pkg.startswith(pw_module_pn)
        if not is_spa_pkg and not is_pw_module_pkg:
            continue

        if pkg in spa_metapkg_rdepends or pkg in pw_module_metapkg_rdepends:
            continue

        # See if the package is empty by looking at the contents of its
        # PKGDEST subdirectory. If this subdirectory is empty, then then
        # package is empty as well. Empty packages do not get added to
        # the meta package's RDEPENDS.
        pkgdir = os.path.join(pkgdest, pkg)
        if os.path.exists(pkgdir):
            dir_contents = os.listdir(pkgdir) or []
        else:
            dir_contents = []
        is_empty = len(dir_contents) == 0
        if not is_empty:
            if is_spa_pkg:
                spa_metapkg_rdepends.append(pkg)
            if is_pw_module_pkg:
                pw_module_metapkg_rdepends.append(pkg)

    d.setVar('RDEPENDS:' + spa_metapkg, ' '.join(spa_metapkg_rdepends))
    d.setVar('DESCRIPTION:' + spa_metapkg, spa_pn + ' meta package')

    d.setVar('RDEPENDS:' + pw_module_metapkg, ' '.join(pw_module_metapkg_rdepends))
    d.setVar('DESCRIPTION:' + pw_module_metapkg, pw_module_pn + ' meta package')
}

PACKAGES =+ "\
    libpipewire \
    ${PN}-tools \
    ${PN}-pulse \
    ${PN}-alsa \
    ${PN}-jack \
    ${PN}-v4l2 \
    ${PN}-spa-plugins \
    ${PN}-spa-plugins-meta \
    ${PN}-spa-tools \
    ${PN}-modules \
    ${PN}-modules-meta \
    ${PN}-alsa-card-profile \
    gstreamer1.0-pipewire \
"

PACKAGES_DYNAMIC = "^${PN}-spa-plugins.* ^${PN}-modules.*"

SYSTEMD_SERVICE:${PN} = "pipewire.service"
CONFFILES:${PN} += "${datadir}/pipewire/pipewire.conf"
FILES:${PN} = " \
    ${datadir}/pipewire/pipewire.conf \
    ${datadir}/pipewire/filter-chain \
    ${systemd_user_unitdir}/pipewire.* \
    ${bindir}/pipewire \
"

FILES:${PN}-dev += " \
    ${libdir}/${PW_MODULE_SUBDIR}/jack/libjack*.so \
"

CONFFILES:libpipewire += "${datadir}/pipewire/client.conf"
FILES:libpipewire = " \
    ${datadir}/pipewire/client.conf \
    ${libdir}/libpipewire-*.so.* \
"
# Add the bare minimum modules and plugins required to be able
# to use libpipewire. Without these, it is essentially unusable.
RDEPENDS:libpipewire += " \
    ${PN}-modules-client-node \
    ${PN}-modules-protocol-native \
    ${PN}-spa-plugins-support \
"

FILES:${PN}-tools = " \
    ${bindir}/pw-* \
"

# This is a shim daemon that is intended to be used as a
# drop-in PulseAudio replacement, providing a pulseaudio-compatible
# socket that can be used by applications that use libpulse.
CONFFILES:${PN}-pulse += "${datadir}/pipewire/pipewire-pulse.conf"

FILES:${PN}-pulse = " \
    ${datadir}/pipewire/pipewire-pulse.conf \
    ${systemd_user_unitdir}/pipewire-pulse.* \
    ${bindir}/pipewire-pulse \
"
RDEPENDS:${PN}-pulse += " \
    ${PN}-modules-protocol-pulse \
"

# alsa plugin to redirect audio to pipewire
FILES:${PN}-alsa = "\
    ${libdir}/alsa-lib/* \
    ${datadir}/alsa/alsa.conf.d/* \
"

#lib to emulate v4l2 system calls on top of PipeWire
FILES:${PN}-v4l2 = "\
    ${libdir}/${PW_MODULE_SUBDIR}/v4l2/libpw-v4l2.so \
"

# jack drop-in libraries to redirect audio to pipewire
CONFFILES:${PN}-jack = "${datadir}/pipewire/jack.conf"
FILES:${PN}-jack = "\
    ${datadir}/pipewire/jack.conf \
    ${libdir}/${PW_MODULE_SUBDIR}/jack/libjack*.so.* \
"
# Dynamic packages (see set_dynamic_metapkg_rdepends).
FILES:${PN}-spa-plugins-bluez5 += " \
    ${datadir}/${SPA_SUBDIR}/bluez5/bluez-hardware.conf \
"
RRECOMMENDS:${PN}-spa-plugins += "${PN}-spa-plugins-meta"

FILES:${PN}-spa-tools = " \
    ${bindir}/spa-* \
"

# Dynamic packages (see set_dynamic_metapkg_rdepends).
FILES:${PN}-modules = ""
RRECOMMENDS:${PN}-modules += "${PN}-modules-meta"

CONFFILES:${PN}-modules-rtkit = "${datadir}/pipewire/client-rt.conf"
FILES:${PN}-modules-rtkit += " \
    ${datadir}/pipewire/client-rt.conf \
    "

FILES:${PN}-alsa-card-profile = " \
    ${datadir}/alsa-card-profile/* \
    ${nonarch_base_libdir}/udev/rules.d/90-pipewire-alsa.rules \
"

FILES:gstreamer1.0-pipewire = " \
    ${libdir}/gstreamer-1.0/* \
"
