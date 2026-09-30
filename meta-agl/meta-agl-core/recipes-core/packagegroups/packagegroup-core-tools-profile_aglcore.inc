# Disable lttng-tools by default since we support several BSPs
# where it does not build due to kernel mismatches (typically
# kernel being too new).
LTTNGTOOLS = ""
