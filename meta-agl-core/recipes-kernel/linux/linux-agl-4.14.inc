FILESEXTRAPATHS:prepend := "${THISDIR}/linux-4.14:"

SRC_URI:append = "\
       file://net-sch_generic-add-if_afp.h-header-to-get-ARPHRD_CA.patch \
       file://net-sch_generic-Use-pfifo_fast-as-fallback-scheduler.patch \
       "
