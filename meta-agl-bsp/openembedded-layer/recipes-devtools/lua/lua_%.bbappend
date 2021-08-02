# this causes a crash in the selftest db.lua !
# Bug-AGL: SPEC-3828
# Upstream status: pending
SRC_URI:remove = "file://CVE-2020-15945.patch"
