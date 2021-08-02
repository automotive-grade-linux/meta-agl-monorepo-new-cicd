do_install:append() {
   echo "System::Pipewire * * http://tizen.org/privilege/internal/dbus yes forever" >> ${D}${sysconfdir}/security/cynagora.initial
}

