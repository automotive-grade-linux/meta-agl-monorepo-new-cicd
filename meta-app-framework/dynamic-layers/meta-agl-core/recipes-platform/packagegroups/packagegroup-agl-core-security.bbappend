require ${@bb.utils.contains('APPFW_ENABLED', '1', '${PN}_appfw.inc', '', d)}
