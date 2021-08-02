# This is a workaround for the race condition between do_image_wic and other
# do_image_* tasks. It ensures that no other do_image_* task can be running and
# reading the rootfs directory at the same time as do_image_wic is modifying
# the /etc/fstab file in the rootfs directory.
#
# A much better fix has been submitted upstream [1], but we can't carry those
# modifications to `scripts/lib/wic` from poky easily in meta-agl. So instead
# we have this workaround until the upstream fix is accepted and backported.
#
# Serializing the image functions as done here impacts build speed but it's
# better to be slow and correct than to be fast and wrong.
#
# [1]: https://lists.openembedded.org/g/openembedded-core/topic/patch_5_6_wic_copy_rootfs/79592787,
#      https://lists.openembedded.org/g/openembedded-core/topic/patch_6_6_wic_optimise/79592788
#      and related patches.
#
# Bug-AGL: SPEC-3621

def get_workaround_wic_typedeps(d):
    if d.getVar('USING_WIC'):
        fstypes = d.getVar('IMAGE_FSTYPES').split()
        basetypes = set()
        for fs in fstypes:
            # Add the basetype to our set of basetypes in use
            t = fs.split('.')[0]
            if t != "wic":
                basetypes.add(t)
        return ' '.join(basetypes)
    else:
        return ''

IMAGE_TYPEDEP:wic += "${@get_workaround_wic_typedeps(d)}"
