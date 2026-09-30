#!/bin/sh

# Update labels on files generated on first boot.
/usr/sbin/restorecon -FRi /etc/systemd /etc/machine-id
if [ $? -eq 0 ]; then
	# Disable parent service
	# NOTE: The service does not use the first boot functionality
	#       in systemd as /etc/machine-id is not writeable until
	#       after it is complete.
	systemctl disable systemd-selinux-relabel.service
fi
exit 0
