# Disable systemd's getty unit generator for guests
PACKAGECONFIG:remove:aglcontainerguest = "serial-getty-generator"