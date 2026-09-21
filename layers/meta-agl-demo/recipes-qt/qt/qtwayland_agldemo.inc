FILESEXTRAPATHS:append := ":${THISDIR}/qtwayland"

SRC_URI:append = "\
    file://0001-qwaylandwindow-Short-circuit-isExposed.patch \
    file://0001-qwaylandintegration-Keep-previous-check-related-to-i.patch \
    "
