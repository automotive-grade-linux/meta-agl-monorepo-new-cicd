require libafb-helpers_git.inc

inherit cmake

RDEPENDS:${PN}:append = " af-binder"

ALLOW_EMPTY:${PN} = "1"

