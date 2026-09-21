# Workaround pip 25.3 changing behavior for old non-PEP517-compliant
# packaging configurations
do_install:prepend() {
    export PIP_NO_BUILD_ISOLATION=0
}
