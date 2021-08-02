SUMMARY = "AGL Application Framework core packages"
DESCRIPTION = "The set of packages required by the AGL Application Framework"
LICENSE = "MIT"

inherit packagegroup

PACKAGES_${PN} = "\
    packagegroup-agl-app-framework \
    "

ALLOW_EMPTY:${PN} = "1"

RDEPENDS:${PN} = "\
	af-binder \
	libafbwsc \
	af-main \
	nss-localuser \
	af-platform-setup \
	"
