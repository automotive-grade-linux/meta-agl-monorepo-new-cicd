SUMMARY = "Utilities for testing of AGL"
DESCRIPTION = "A set of common packages required by testing AGL for Quality Assurance"

LICENSE = "MIT"

inherit packagegroup

PACKAGES = "\
    packagegroup-agl-test \
    packagegroup-agl-test-ltp \
    packagegroup-ivi-common-test \
    "

ALLOW_EMPTY:${PN} = "1"

RDEPENDS:${PN} += "\
    bc \
    ebizzy \
    evtest \
    ffsb \
    glmark2 \
    interbench \
    ipv6connect \
    linpack \
    linus-stress \
    nmap \
    rt-tests \
    stress \
    "





# to be added, but needs LICENSE_FLAGS_WHITELIST="non-commercial"
#    netperf                             # meta-networking

# relocate due to dependencies
#    iperf3 \                            # meta-oe
#    lmbench \                           # meta-oe
#    dbench \                            # meta-oe
#    nbench-byte \                       # meta-oe
#    iozone3 \                           # meta-oe
#

# FTBS, SPEC-316
#    himeno                              # nowhere
# FTBS, SPEC-1384
#    trinity                             # meta-linaro (1.5)

#    packagegroup-agl-test-ltp \
#    ltp \                               # ltp in oe-core
#

# Packages for shell commands which are required by LTP
#   readelf, logrotate, vsftpd, crontab, sar, arp, ftp,
#   host, rcp, rlogin, rsh, tcpdump, expect, iptables, dnsmasq,
#   pgrep
RDEPENDS:packagegroup-agl-test-ltp = ""
#RDEPENDS:packagegroup-agl-test-ltp += " \
#    initscripts-functions bind-utils binutils \
#    cronie dnsmasq expect inetutils-ftp inetutils-rsh \
#    iptables logrotate net-tools sysstat tcpdump vsftpd \
#    "

RDEPENDS:packagegroup-ivi-common-test = " \
    packagegroup-agl-test \
    "
