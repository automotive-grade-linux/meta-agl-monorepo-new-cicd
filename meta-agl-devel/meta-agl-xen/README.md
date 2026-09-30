# AGL Xen support base layer

This OpenEmbedded layer adds the feature 'agl-xen'

'agl-xen' is used to provide base Xen image building support via:
- Linux kernel configuration fragments to support Xen dom0 vs domd/domu.
  Also disables meta-virtualization's Xen kernel configuration, but
  leaves that layer's other kernel configuration support alone in case
  a downstream user wants e.g. Kubernetes support.
- agl-xen-image bbclass for multi-partition disk images to support
  booting Xen and having dom0/domd/domu partitions.  More documentation
  is available at the top of classes-recipe/agl-xen-image.bbclass, and
  the agl-xen-image-test image serves as an example.
- Example multiconfig configuration for building guest images.

The intent is that this layer stay as platform agnostic as possible, or
that any platform-specific support be as high level and reusable as
possible like the adaptations to make meta-virtualization's Raspberry Pi
support work with agl-xen-image.bbclass.

## Kernel Configuration

Adding 'agl-xen-dom0', 'agl-xen-domd', or 'agl-xen-domu' to the
AGL_FEATURES variable will pull in corresponding kernel configuration
fragments.  The 'agl-xen-domu' and 'agl-xen-domd' multiconfig definitions
append to AGL_FEATURES appropriately to do this, and the intent is they
are potentially reusable for simple downstream build configurations.

## Setup

Enable the `agl-xen` AGL feature when setting up your build environment
with aglsetup.sh.
