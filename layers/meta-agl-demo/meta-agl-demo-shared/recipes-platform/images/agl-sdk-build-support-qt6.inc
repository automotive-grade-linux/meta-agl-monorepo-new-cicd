# SDK buld support for Qt6
# This file should be included by image recipe

# Qt6 SDK building
# populate_sdk_qt6_base is used to match the target libraries 
# populate_sdk_qt6 would install all add-ons

inherit populate_sdk_qt6_base
TOOLCHAIN_HOST_TASK += "nativesdk-packagegroup-qt6-toolchain-host-essentials"
