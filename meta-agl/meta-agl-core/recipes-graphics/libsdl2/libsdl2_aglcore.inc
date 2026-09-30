# Make sure opengl is disabled, as using wayland implies gles2 in the
# libsdl2 PACKAGECONFIG definitions.
PACKAGECONFIG:remove = "opengl"
