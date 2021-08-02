# Include file intended for kernel.bbclass based recipes that do not
# have their own config fragment merging scheme.

DEPENDS += "kern-tools-native"

include linux-agl-config.inc

# returns all the elements from the src uri that are .cfg files
def find_cfgs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        if s.endswith('.cfg'):
            sources_list.append(s)

    return sources_list

do_configure:append () {
    if [ -n "${AGL_KCONFIG_FRAGMENTS}" ]; then
        [ ! -f .config ] && cp -a ${WORKDIR}/defconfig .config
        merge_config.sh -m .config ${@" ".join(find_cfgs(d))}
        yes '' | make oldconfig
    fi
}
