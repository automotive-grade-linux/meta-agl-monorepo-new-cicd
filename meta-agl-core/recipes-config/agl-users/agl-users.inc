inherit useradd

USERADD_PACKAGES = "${PN}"
USERADDEXTENSION = "useradd-staticids"

GROUPADD_PARAM:${PN} = "\
    --system video ; \
    --system pipewire ; \
    -g 1001 agl-driver ; \
"

USERADD_PARAM:${PN} = "\
    -g 1001 -u 1001 -G video,pipewire -o -d /home/agl-driver -m -K PASS_MAX_DAYS=-1 agl-driver ; \
"
