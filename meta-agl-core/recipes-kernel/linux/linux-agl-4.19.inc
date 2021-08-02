FILESEXTRAPATHS:prepend := "${THISDIR}/linux-4.19:"

SRC_URI:append = "\
       file://net-sch_generic-Use-pfifo_fast-as-fallback-scheduler.patch \
       "
