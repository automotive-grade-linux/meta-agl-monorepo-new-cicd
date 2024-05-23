SUMMARY = "AGL Store Application"
DESCRIPTION = "A secure, user-friendly Flatpak App Store for AGL In-Vehicle Infotainment (IVI) systems"
AUTHOR = "Ahmed Wafdy"
HOMEPAGE = "https://github.com/toyota-connected/tcna-packages"
SECTION = "graphics"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d73cf6ba84211d8b7fd0d2865b678fe8"

SRCREV = "44de1e21bbe1701a498d8303385f116cb07fd708"
SRC_URI = "git://github.com/toyota-connected/tcna-packages.git;lfs=0;branch=v2.0;protocol=https"

inherit flutter-app agl-app

PUBSPEC_APPNAME = "flatpak_flutter_example"
FLUTTER_BUILD_ARGS = "bundle -v"
PUBSPEC_IGNORE_LOCKFILE = "1"
FLUTTER_APPLICATION_PATH = "packages/flatpak/example"

AGL_APP_TEMPLATE = "agl-app-flutter"
AGL_APP_NAME = "AGL Store"
AGL_APP_ID = "aglstore"
