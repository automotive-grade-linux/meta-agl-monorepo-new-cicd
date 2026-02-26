# Include SDK build support common
require recipes-platform/images/agl-sdk-build-support.inc


# Enable IVI SDK support
# Add grpc for building natively for grpc_cpp_plugin
TOOLCHAIN_HOST_TASK += "nativesdk-grpc nativesdk-grpc-dev"
