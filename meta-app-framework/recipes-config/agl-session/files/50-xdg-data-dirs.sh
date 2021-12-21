#!/bin/sh

# use a default value if not already set
XDG_DATA_DIRS="${XDG_DATA_DIRS:-/usr/local/share/:/usr/share}"

# write our output
echo "XDG_DATA_DIRS=${XDG_DATA_DIRS}"
