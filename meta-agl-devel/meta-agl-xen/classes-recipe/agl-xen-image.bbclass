#
# AGL partitioned Xen image
#
# This class creates a disk image that pulls together Xen and
# required guest domain images.  The domain images may either be build
# dependencies or sourced externally.  The resulting disk image has a
# partition layout of:
#
# - boot partition
#   - Xen and any bootloader files.  dom0 files may optionally
#     be installed here to support certain bootloaders.
# - dom boot partition
#   - Contains dom kernels and any other extra files in
#     subdirectories named for the domain (name can be individually
#     overridden as desired).  If a dom0 partition is configured,
#     but the dom0 files have been configured to go into the boot
#     partition, they will not be installed.  The expectation is
#     that IMAGE_BOOT_FILES will be used to install the dom0 files
#     in this case.
# - dom0 image partition
#   - optional
# - domd image partition
#   - optional
# - any number of domu image partitions
#   - optional
#
# See below for the specific configuration options for domains.
# Note that:
# 1) The partition numbering for the domain images depends
#    on partition table choice (DOS or GPT) and the number and
#    size of the domain partitions.  They will be more predictable
#    with GPT partitioning.
# 2) IMAGE_INSTALL has no effect on the contents of the dom boot
#    partition, and the rootfs pre and post command hooks are
#    disabled.  Extra files for each domain may be installed via
#    the configuration variable described below.  If something
#    more elaborate is required, this may be revisited.
# 3) The base WKS file used for generating the disk image comes
#    from the standard WKS_FILE variable to remain compatible with
#    BSP layers.  It is possible that the WKS file provided by
#    default by some BSP layers may not work with this scheme, and
#    a custom version may be necessary.
#
#
# Configuration variables
#
# Global:
#
# DOM0_IMAGE
# dom0 image name.  This is optional, and may be a multiconfig image
# specified as <multiconfig name>:<image name> (without the "mc:"
# prefix).
#
# DOMD_IMAGE
# domd image name.  This is optional, and may be a multiconfig image
# specified as <multiconfig name>:<image name> (without the "mc:"
# prefix).
#
# DOMU_IMAGES
# List of domu image names.  This is optional, and each image may be a
# multiconfig image specified as <multiconfig name>:<image name>
# (without the "mc:" prefix).
#
# DOM0_FILES_IN_BOOT
# Flag to indicate that dom0 boot artifacts, i.e. kernel and any extra
# files, will be installed in the boot partition by the bootimg plugin
# used.  The IMAGE_BOOT_FILES or IMAGE_EFI_BOOT_FILES variables may
# need to be changed if extra files need to be installed.  At present
# this is really only required if the dom0 artifacts are strictly
# required in the boot partition, which can be the case when booting
# with bootloaders other than U-Boot without using a Xen stubdom like
# pvgrub.
#
# If future usecases require the boot artifacts for other domains in
# the boot partition, it is likely that this class will need to be
# refactored into a wic plugin.  For now, default to installing dom0
# files into the "dom boot" partition for consistency.
#
# DOM_IMAGES_CLEAN
# Flag to control whether image files linked/copied into DEPLOYDIR_IMAGE
# for wic should be cleaned up after the image is built.  This avoids
# bloating the deploy dir with the guest image files, and should usually
# be left enabled.
#
# Per-domain:
#
# DOM_MACHINE_<domain image>
# Overrides expected MACHINE value for the domain.  This is useful if a
# domain image is built with a multiconfig that uses a MACHINE other
# than DOMU_MACHINE (e.g. for Zephyr).
#
# DOM_EXTERNAL_DIR_<domain image>
# Overrides the directory where a domain's files will be installed from,
# rather than using the expected DEPLOY_DIR_IMAGE for a domain image's
# multiconfig value.  Note that if set, no build dependencies will be
# generated for a domain's files.
#
# DOM_KERNEL_<domain image>
# Overrides the kernel filename for a domain, rather than using the
# default value of KERNEL_IMAGETYPE-<machine>.bin.
#
# DOM_EXTRA_FILES_<domain image>
# Files to install for a domain other than the kernel.  They are
# expected to be in the deploy directory for the domain's build
# configuration, or in the specified external directory.
#
# DOM_NAME_<domain image>
# Overrides the name of the directory the domain's boot files are
# installed into in the domain boot partition.  The default values are
# 'dom0' and 'domd' if those domains are present, and 'domu-<x>' for any
# domu domains, with 'x' starting at 1.  No checking is done for
# conflicting names.
#
# DOM_IMAGE_<domain image>
# Overrides the filename for the domain's root filesystem image.
# The derived default value is
# '<domain image>-<dom machine>${IMAGE_NAME_SUFFIX}.ext4'.
# If not an absolute path, the file is expected to be in the deploy
# directory of the domain, or the specified external directory.
# If it is specified with an absolute path, that will be used as is.
#
# DOM_SIZE_<domain image>
# Overrides the partition size used for the domain image.  wic will
# size the partition to the given image filesystem size, set this if
# extra space or a fixed size is desired.  No checking is done in
# advance that the given size is big enough for the image, and it is
# possible that the partition numbering of the partition and following
# paritions may change depending on size with the DOS partition table
# scheme.
#


# Reuse the image bbclass, but clear out anything that would install
# files, as we only want deploy artifacts installed for Xen to use.
inherit image
ROOTFS_BOOTSTRAP_INSTALL = ""
ROOTFS_PREPROCESS_COMMAND = ""
IMAGE_FEATURES = ""
IMGCLASSES = "image_types image_types_wic"

# Default to installing dom0 files into the "dom boot" partition.
DOM0_FILES_IN_BOOT ??= "0"
# None of the bootloader options for x86 platforms seem like they can
# handle loading a kernel from a second partition for Xen to use, so
# default to installing dom0 files in the boot partition there.
# If a workable solution for that becomes available (perhaps pvgrub),
# this can be changed.
DOM0_FILES_IN_BOOT:x86-64 = "1"

# Default to cleaning up linked/copied guest image files.
DOM_IMAGES_CLEAN = "1"

#
# Default (empty) configuration
#

DOMU_MACHINE ??= "${AGL_GUEST_MACHINE}"

DOM0_IMAGE ??= ""
DOMD_IMAGE ??= ""
DOMU_IMAGES ??= ""

#
# Dependency handling
#

# We require Xen to be built and deployed before building an image
do_image_wic[depends] += "xen:do_deploy"

def handle_image_dep(d, image):
    if not image:
        return

    depends = 'depends'
    if ":" in image:
        (mc, image) = image.split(':')
        depends = 'mcdepends'
        dependency = 'mc::' + mc + ':' + image + ':do_image_complete'
    else:
        dependency = image + ':do_image_complete'
    external_dir = d.getVar('DOM_EXTERNAL_DIR_' + image) or ''
    if not external_dir:
        d.appendVarFlag('do_rootfs', depends, ' ' + dependency)

    # Hook up optional domain variables as do_rootfs input dependencies
    dom_var_prefixes = 'DOM_MACHINE DOM_EXTERNAL_DIR DOM_KERNEL DOM_EXTRA_FILES DOM_NAME DOM_IMAGE DOM_SIZE'
    for prefix in dom_var_prefixes.split():
        d.appendVarFlag('do_rootfs', 'vardeps', ' ' + prefix + '_' + image)

python __anonymous() {
    images = ' '.join([(d.getVar('DOM0_IMAGE') or ''), (d.getVar('DOMD_IMAGE') or ''), (d.getVar('DOMU_IMAGES') or '')])
    for image in images.split():
        handle_image_dep(d, image)

    d.setVar('WKS_FULL_PATH_IN', d.getVar('WKS_FULL_PATH'))
    d.setVar('WKS_FULL_PATH', os.path.join(d.getVar('WORKDIR'), 'agl-xen-image-generated.wks'))

    # Hook up do_rootfs variable dependencies
    d.appendVarFlag('do_rootfs', 'vardeps', ' DOM0_IMAGE DOMD_IMAGE DOMU_IMAGES')

    if d.getVar('DOM0_FILES_IN_BOOT') == '0':
        # Remove kernel from boot partition to save space and avoid confusion.
        # This avoids needing to set IMAGE_BOOT_FILES:remove manually for a lot
        # of BSPs.
        kernel = d.getVar('KERNEL_IMAGETYPE') or ''
        if kernel:
            image_files = d.getVar('IMAGE_BOOT_FILES') or ''
            image_files_new = ''
            for file in image_files.split():
                if file != kernel:
                    image_files_new = ' '.join([image_files_new, file])
            if image_files_new:
                d.setVar('IMAGE_BOOT_FILES', image_files_new)
}

#
# Image building
#

def init_wks_file(src_filename, dst_file):
    try:
        with open(src_filename, "r") as src_file:
            for line in src_file:
                # Potentially can filter out lines here if necessary
                dst_file.write(line)
    except IOError:
        bb.fatal('Could not read %s' % src_filename)

def update_wks_file(dst_file, image_file, part_size):
    try:
        part_cmd = 'part --source rawcopy --sourceparams="file=' + image_file + '"'
        if part_size:
            part_cmd += ' --size=' + part_size
        dst_file.write(part_cmd + '\n')
    except IOError:
        bb.fatal('Could not write to %s' % dst_file.name)

def install_guest_artifacts(d, target, default_name, dst_wks_file, dom_images_file, install_boot_files = True):
    import shutil

    mc = ''
    image = target
    if ":" in target:
        (mc, image) = target.split(':')

    deploy_dir = d.getVar('DEPLOY_DIR_IMAGE')
    src_dir = deploy_dir
    dom_machine = d.getVar('MACHINE')
    if mc:
        dom_machine = d.getVar('DOM_MACHINE_' + image) or d.getVar('DOMU_MACHINE')
        src_dir = os.path.join(d.getVar('TOPDIR'), 'tmp-' + mc, 'deploy', 'images', dom_machine)

    # Override source dir for externally built images
    external_dir = d.getVar('DOM_EXTERNAL_DIR_' + image) or ""
    if external_dir:
        if not os.path.isdir(external_dir):
            bb.fatal('External directory %s does not exist' % external_dir)
        else:
            bb.note('Using external directory %s for %s' % (external_dir, image))

        src_dir = external_dir

    kernel_default = d.getVar('KERNEL_IMAGETYPE') + '-' + dom_machine + '.bin'
    kernel = d.getVar('DOM_KERNEL_' + image) or kernel_default
    files = ' '.join([kernel, (d.getVar('DOM_EXTRA_FILES_' + image) or "")])

    if install_boot_files:
        # Install domain files into rootfs
        name = d.getVar('DOM_NAME_' + image) or default_name
        dst_dir = os.path.join(d.getVar("IMAGE_ROOTFS"), name)
        bb.utils.mkdirhier(dst_dir)
        for f in files.split():
            shutil.copy(os.path.join(src_dir, f), dst_dir)
    elif mc or external_dir:
        # Need to copy domain files to DEPLOYDIR_IMAGE for wic boot partition plugins
        bb.utils.mkdirhier(deploy_dir)
        for f in files.split():
            shutil.copy(os.path.join(src_dir, f), deploy_dir)

    # Link domain image into DEPLOY_DIR_IMAGE for wic
    image_file = os.path.join(src_dir, image + '-' + dom_machine + (d.getVar('IMAGE_NAME_SUFFIX') or '') + '.ext4')
    image_file_override = d.getVar('DOM_IMAGE_' + image) or ''
    if image_file_override:
        if not os.path.isabs(image_file_override):
            # Assume it is a file in the external or deploy dir
            if external_dir:
                image_file = os.path.join(external_dir, image_file_override)
            else:
                image_file = os.path.join(deploy_dir, image_file_override)
        else:
            image_file = image_file_override

    if os.path.dirname(image_file) != deploy_dir:
        # Link multiconfig or external images into deploy dir
        bb.utils.mkdirhier(deploy_dir)
        src_file = image_file
        dst_file = os.path.join(deploy_dir, os.path.basename(image_file))
        if os.path.islink(image_file):
            src_file = os.path.realpath(image_file)
        if os.path.exists(dst_file):
            os.remove(dst_file)
        bb.note("Linking %s" % src_file)
        oe.path.copyhardlink(src_file, dst_file)
        dom_images_file.write(dst_file + '\n')

    # Write domain partition to wks file
    update_wks_file(dst_wks_file, os.path.basename(image_file), d.getVar('DOM_SIZE_' + image) or '')


# Override to avoid any package installation.
# This is a less expensive way to get an empty rootfs than letting
# the default version run and then erasing the result.
fakeroot python do_rootfs () {
    # Initialize generated wks file
    src_wks_filename = d.getVar('WKS_FULL_PATH_IN') or ''
    if not (src_wks_filename and os.path.exists(src_wks_filename)):
        bb.fatal('WKS_FILE not set or missing')
    dst_wks_filename = d.getVar('WKS_FULL_PATH')
    try:
        dst_wks_file = open(dst_wks_filename, "w")
    except IOError:
        bb.fatal('Could not open $s' % dst_wks_filename)
    init_wks_file(src_wks_filename, dst_wks_file)

    # File to save images that are copied to the deploy directory for
    # later cleanup
    dom_images_filename = os.path.join(d.getVar('WORKDIR'), 'deployed_dom_images')
    try:
        dom_images_file = open(dom_images_filename, "w")
    except IOError:
        bb.fatal('Could not open $s' % dom_images_filename)

    dom0 = d.getVar('DOM0_IMAGE') or ''
    if dom0:
        install_guest_artifacts(d, dom0, 'dom0', dst_wks_file, dom_images_file,
                                install_boot_files = d.getVar('DOM0_FILES_IN_BOOT') != '1')

    domd = d.getVar('DOMD_IMAGE') or ''
    if domd:
        install_guest_artifacts(d, domd, 'domd', dst_wks_file)

    domu_targets = d.getVar('DOMU_IMAGES') or ''
    i = 1
    for target in domu_targets.split():
        install_guest_artifacts(d, target, ('domu-%d' % i), dst_wks_file, dom_images_file)
        i += 1
    dst_wks_file.close()
    dom_images_file.close()
}

python do_dom_image_clean () {
    if d.getVar('DOM_IMAGES_CLEAN') == '1':
        dom_images_filename = os.path.join(d.getVar('WORKDIR'), 'deployed_dom_images')
        with open(dom_images_filename, "r") as file:
            for line in file:
                image = line.rstrip()
                if os.path.exists(image):
                    bb.note('Removing copied domain image %s' % os.path.basename(image))
                    os.remove(image)
}

addtask do_dom_image_clean after do_image_wic before do_image_complete
