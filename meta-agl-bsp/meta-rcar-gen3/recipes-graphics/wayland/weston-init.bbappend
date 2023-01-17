# Add display group to default list for weston user
USERADD_PARAM:${PN} = "--home /home/weston --shell /bin/sh --user-group -G video,input,display weston"

GROUPADD_PARAM:${PN} = "--system display ; -r wayland"
