# Allow input device for agl-driver
GROUPADD_PARAM:${PN} = "\
	--system video ; \
	--system input ; \
	--system pipewire ; \
	-g 1001 agl-driver ; \
"
USERADD_PARAM:${PN} = "\
  -g 1001 -u 1001 -G video,input,pipewire -o -d /home/agl-driver -m -K PASS_MAX_DAYS=-1 agl-driver ; \
"
