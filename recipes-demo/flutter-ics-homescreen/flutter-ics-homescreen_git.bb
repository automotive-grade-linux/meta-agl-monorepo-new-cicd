SUMMARY = "AGL ICS Flutter Homescreen"
DESCRIPTION = "Demo Flutter homescreen for Automotive Grade Linux by ICS."
HOMEPAGE = "https://gerrit.automotivelinux.org/gerrit/apps/flutter-ics-homescreen"
SECTION = "graphics"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

SRC_URI = "git://gerrit.automotivelinux.org/gerrit/apps/flutter-ics-homescreen;protocol=https;branch=${AGL_BRANCH} \
  file://ics-homescreen.toml \
  file://flutter-ics-homescreen.service \
  file://flutter-ics-homescreen.env \
  file://kuksa.toml \
  file://flutter-ics-homescreen.token \
  file://radio-presets.toml \
  file://kvm.conf \
"
SRCREV = "3cad94699fc067055ee71154be9b09efc8b17151"

S = "${WORKDIR}/git"

PUBSPEC_APPNAME = "flutter_ics_homescreen"

inherit flutter-app systemd

APP_CONFIG = "ics-homescreen.toml"

PUBSPEC_IGNORE_LOCKFILE = "1"

SYSTEMD_SERVICE:${PN} = "flutter-ics-homescreen.service"

# Disable the background animation on all platforms except the Renesas M3/H3 for now
DISABLE_BG_ANIMATION = "-DDISABLE_BKG_ANIMATION=true"
DISABLE_BG_ANIMATION:rcar-gen3 = ""
APP_AOT_EXTRA:append = " ${DISABLE_BG_ANIMATION}"

# Check for agl-offline-voice-agent feature
ENABLE_VOICE_ASSISTANT = "${@bb.utils.contains('EXTRA_IMAGE_FEATURES', 'agl-offline-voice-agent', '-DENABLE_VOICE_ASSISTANT=true', '-DENABLE_VOICE_ASSISTANT=false', d)}"
APP_AOT_EXTRA:append = " ${ENABLE_VOICE_ASSISTANT}"

do_install:append() {
    install -D -m 0644 ${WORKDIR}/${BPN}.service ${D}${systemd_system_unitdir}/${BPN}.service

    install -D -m 0644 ${WORKDIR}/${BPN}.env ${D}${sysconfdir}/default/${BPN}

    install -D -m 0644 ${WORKDIR}/kvm.conf ${D}${systemd_system_unitdir}/${BPN}.service.d/kvm.conf

    # VIS authorization token file for KUKSA.val should ideally not
    # be readable by other users, but currently that's not doable
    # until a packaging/sandboxing/MAC scheme is (re)implemented or
    # something like OAuth is plumbed in as an alternative.
    install -d ${D}${sysconfdir}/xdg/AGL/flutter-ics-homescreen
    install -m 0644 ${WORKDIR}/kuksa.toml ${D}${sysconfdir}/xdg/AGL/flutter-ics-homescreen/
    install -m 0644 ${WORKDIR}/flutter-ics-homescreen.token ${D}${sysconfdir}/xdg/AGL/flutter-ics-homescreen/
    install -m 0644 ${WORKDIR}/radio-presets.toml ${D}${sysconfdir}/xdg/AGL/flutter-ics-homescreen/
}

FILES:${PN} += "${datadir} ${sysconfdir}/xdg/AGL ${sysconfdir}/default"

RDEPENDS:${PN} += " \
    flutter-auto \
    agl-flutter-env \
    applaunchd \
"

# systemd override to add network-online.target dependency for KVM setups
PACKAGE_BEFORE_PN += "${PN}-conf-kvm"
FILES:${PN}-conf-kvm += "${systemd_system_unitdir}/flutter-ics-homescreen.service.d/kvm.conf"
RDEPENDS:${PN}-conf-kvm = "${PN}"

do_compile[network] = "1"
