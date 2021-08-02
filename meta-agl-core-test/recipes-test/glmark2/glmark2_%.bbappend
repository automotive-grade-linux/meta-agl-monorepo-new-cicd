# Update glmark2
SRCREV = "784aca755a469b144acf3cae180b6e613b7b057a"
# Already in newer version
SRC_URI:remove = "file://python3.patch"

PACKAGECONFIG = "wayland-gles2"
PACKAGECONFIG[wayland-gles2] = ",,virtual/egl virtual/libgles2 wayland wayland-protocols wayland-native"
