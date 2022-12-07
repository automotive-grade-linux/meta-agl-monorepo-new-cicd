#
# AGL application systemd unit installation class
#

# Systemd template unit
# * agl-app, agl-app-web, agl-app-flutter valid
AGL_APP_TEMPLATE ?= "agl-app"

# Application ID
# This is what the application will be referred to in the list
# exposed to clients by applaunchd, and generally ends up as the
# identifier used by agl-compositor for application surface
# activation.
AGL_APP_ID ?= "${BPN}"

# Application display name
AGL_APP_NAME ?= "${AGL_APP_ID}"

# Application executable
# * agl-app template only
# Use if the application ID and the executable name are both
# different from the package name and each other as well.
AGL_APP_EXEC ?= "${AGL_APP_ID}"

# Web application bundle directory (non-absolute, so directory
# name under /usr/lib/wam_apps)
# * agl-app-web template only
# Use if the web application bundle installs to a directory that
# is not the same as the package name.
AGL_APP_WAM_DIR ?= "${BPN}"

do_install:append () {
    install -d ${D}${systemd_system_unitdir}
    ln -s ${AGL_APP_TEMPLATE}\@.service \
        ${D}${systemd_system_unitdir}/${AGL_APP_TEMPLATE}\@${AGL_APP_ID}.service

    # NOTE: Unit & Service changes could potentially be collected
    #       and a single override .conf created, but things will be
    #       kept simple for now.

    if [ "${AGL_APP_EXEC}" != "${AGL_APP_ID}" ]; then
        install -d ${D}${systemd_system_unitdir}/${AGL_APP_TEMPLATE}\@${AGL_APP_ID}.service.d
        cat <<-EOF > ${D}${systemd_system_unitdir}/${AGL_APP_TEMPLATE}\@${AGL_APP_ID}.service.d/exec.conf
	[Service]
	ExecStart=
	ExecStart=${AGL_APP_EXEC}
	EOF
    fi

    if [ "${AGL_APP_NAME}" != "${AGL_APP_ID}" ]; then
        install -d ${D}${systemd_system_unitdir}/${AGL_APP_TEMPLATE}\@${AGL_APP_ID}.service.d
        cat <<-EOF > ${D}${systemd_system_unitdir}/${AGL_APP_TEMPLATE}\@${AGL_APP_ID}.service.d/name.conf
	[Unit]
	Description=
	Description=${AGL_APP_NAME}
	EOF
    fi

    if [ "${AGL_APP_TEMPLATE}" = "agl-app-web" -a "${AGL_APP_ID}" != "${BPN}" ]; then
        # The application ID does not necessarily match the package name
        # used in the WAM install hierarchy, and the IDs are hard-coded in
        # some of the web apps, so if necessary create an override for the
        # environment variable used in place of directly deriving from %i
        # (which will always be the app id).
        install -d ${D}${systemd_system_unitdir}/${AGL_APP_TEMPLATE}\@${AGL_APP_ID}.service.d
        cat <<-EOF > ${D}${systemd_system_unitdir}/${AGL_APP_TEMPLATE}\@${AGL_APP_ID}.service.d/wam.conf
	[Service]
	Environment=AGL_APP_WAM_DIR=${AGL_APP_WAM_DIR}
	EOF
    fi

    if [ "${AGL_APP_TEMPLATE}" = "agl-app-flutter" ]; then
       # Install icon if present
       if [ -f ${S}/package/${AGL_APP_ID}.svg ]; then
          install -d ${D}${datadir}/icons/hicolor/scalable
          install -m 0644 ${S}/package/${AGL_APP_ID}.svg ${D}${datadir}/icons/hicolor/scalable/
       fi
   fi
}

FILES:${PN}:append = " ${systemd_system_unitdir} ${datadir}/icons"

RDEPENDS:${PN}:append = " applaunchd-template-${AGL_APP_TEMPLATE}"
