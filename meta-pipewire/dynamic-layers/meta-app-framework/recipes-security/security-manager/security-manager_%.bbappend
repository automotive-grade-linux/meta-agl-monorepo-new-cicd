do_install:append() {
   echo "~APP~ System::Pipewire rw" >> ${D}${datadir}/security-manager/policy/app-rules-template.smack
}
