require recipes-devtools/libafb-helpers/libafb-helpers_git.inc

DEPENDS:append = " qtwebsockets"
RDEPENDS:${PN}:append = " af-binder"

inherit cmake_qt5

EXTRA_OECMAKE:append = " -DAFB_HELPERS_QT=ON -DAFB_HELPERS=OFF"

ALLOW_EMPTY:${PN} = "1"

