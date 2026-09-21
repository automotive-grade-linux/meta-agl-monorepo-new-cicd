# Unified HMI Preconfigured Demo Images

## Introduction
Unified HMI requires a somewhat complex environment setup, so this recipe provides instructions to create preconfigured images.

## Unified HMI frameworks
For a detailed explanation of Unified HMI, please refer to the [AGL Documentation](https://docs.automotivelinux.org/en/master/#06_Component_Documentation/11_Unified_HMI/)

## Demo environment
Demo images support three boards: qemux-86-64, raspberrypi4, and agl-refhw. 
Please prepare two boards, one as the sender and the other as the receiver, using any of the supported boards, and build the respective images for each.

* sender
```
IP: 192.168.0.100
HOSTNAME: agl-host0
```

* receiver
```
IP: 192.168.0.101
HOSTNAME: agl-host1
```

## How to build
Follow the [AGL documentation](https://docs.automotivelinux.org/en/master/#01_Getting_Started/02_Building_AGL_Image/01_Build_Process_Overview/) for the build process, and set up the "[Initializing Your Build Environment](https://docs.automotivelinux.org/en/master/#01_Getting_Started/02_Building_AGL_Image/04_Initializing_Your_Build_Environment/)" section as described below to enable the AGL feature 'agl-uhmi-demo'.

For example:
```
$ cd $AGL_TOP/master
$ source ./meta-agl/scripts/aglsetup.sh -m qemux86-64 -b qemux86-64 agl-devel agl-uhmi-demo
```

After adding the feature, execute the bitbake command:

For sender:
```
$ bitbake agl-uhmi-demo-preconfigured-ivi-demo-flutter
```

For receiver:
```
$ bitbake agl-uhmi-demo-preconfigured-weston
```

## How to use
After both the sender and receiver have successfully booted, set the correct IP addresses for both. Then, execute the following commands on the sender to confirm that the application is displayed across two displays, each connected to different boards.

### How to launch application
Execute UCL API to launch the rvgpu compositor and an application (e.g., glmark2).

```
$ ucl-api-comm -c launch_compositor_async
$ ucl-api-comm -c run_async glmark2
```

### How to control layouts
After launching applications, execute ULA API to control application layouts across two displays.
In the preconfigured image, three JSON samples are provided in /var/local/uhmi-app/glmark2/:
* initial-vscreen-wide.json: display app across two displays
* initial-vscreen-agl.json: display app on the AGL display
* initial-vscreen-weston.json: display app on the weston display

You can change application layouts by executing the following commands:
```
$ ula-grpc-client -c DwmSetLayoutCommand /var/local/uhmi-app/glmark2/initial-vscreen-wide.json
$ ula-grpc-client -c DwmSetLayoutCommand /var/local/uhmi-app/glmark2/initial-vscreen-agl.json
$ ula-grpc-client -c DwmSetLayoutCommand /var/local/uhmi-app/glmark2/initial-vscreen-weston.json
```
