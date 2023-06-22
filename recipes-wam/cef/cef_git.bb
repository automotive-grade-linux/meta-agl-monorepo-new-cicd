require gn-utils.inc

LICENSE = "Apache-2.0 & BSD-3-Clause & LGPL-2.0-only & LGPL-2.1-only"

LIC_FILES_CHKSUM = "\
    file://LICENSE;md5=c408a301e3407c3803499ce9290515d6 \
    file://third_party/blink/renderer/core/LICENSE-LGPL-2;md5=36357ffde2b64ae177b2494445b79d21 \
    file://third_party/blink/renderer/core/LICENSE-LGPL-2.1;md5=a778a33ef338abbaf8b8a7c36b6eec80 \
"
CHROMIUM_URL = "github.com/rogerzanoni/chromium108.git"
CHROMIUM_VERSION = "108.0.5359.125"
BRANCH_chromium108 = "5359"
# Taken from https://bitbucket.org/chromiumembedded/cef/src/5615/CHROMIUM_BUILD_COMPATIBILITY.txt
SRCREV_chromium108 = "fe6cbc05f1ec805339364b0df0e8be925a991a80"
SRCREV_cef = "a98cd4cdc0fdc49b6c38ba10cec800922745441e"
GN_TARGET_CPU = "${@gn_arch_name('${TUNE_ARCH}')}"

PV = "${CHROMIUM_VERSION}.${BRANCH_chromium108}+git"

FILESEXTRAPATHS:prepend := "${THISDIR}/files/cef:"
FILESEXTRAPATHS:prepend := "${THISDIR}/files/chromium:"

SRC_URI = "\
    git://${CHROMIUM_URL};branch=${BRANCH_chromium108};protocol=https;rev=${SRCREV_chromium108};name=chromium108;destsuffix=git/chromium/src \
    file://0001-sql-relax-constraints-on-VirtualCursor-layout.patch \
    file://0002-Don-t-look-for-depot_tools-in-chrommium-s-third_part.patch \
    file://0003-Remove-the-GN-settings-done-for-clang-that-conflict-.patch \
    file://0004-Don-t-use-DRI-for-renesas.patch \
    file://0005-Add-the-essential-parts-of-wayland-extensions-and-ag.patch \
    file://0006-Add-webos-agl-waylandwindow-window-tree-host-essenti.patch \
    file://0007-Only-bind-to-agl_shell-if-it-s-the-browser-process.patch \
    file://0008-Add-a-method-to-check-if-the-agl-window-is-configure.patch \
    file://0009-Start-using-agl-shell-version-4.patch \
    \
    git://bitbucket.org/chromiumembedded/cef.git;branch=${BRANCH_chromium108};protocol=https;rev=${SRCREV_cef};name=cef;destsuffix=git/chromium/src/cef \
    file://0001-Add-an-option-to-use-an-output-directory-outside-src.patch;patchdir=cef \
    file://0002-Add-an-option-to-override-the-default-distrib-direct.patch;patchdir=cef \
    file://0003-Add-an-option-to-use-an-alternative-base-output-dire.patch;patchdir=cef \
    file://0004-Add-an-option-to-bypass-sysroot-checking-and-force.patch;patchdir=cef \
    file://0005-Linux-Fix-build-without-X11-fixes-issue-3431.patch;patchdir=cef \
    file://0006-Add-AGL-wayland-window-related-calls.patch;patchdir=cef \
    file://0007-Add-a-method-to-check-if-the-agl-window-is-configure.patch;patchdir=cef \
    file://0008-Add-the-SetActivateRegion-method.patch;patchdir=cef \
    file://0009-Allow-passing-the-app_id-on-widget-creation.patch;patchdir=cef \
    file://0010-Update-generated-api-wrapper-files.patch;patchdir=cef \
"

BASE_DIR = "${WORKDIR}/git/chromium"
CHROMIUM_DIR = "${BASE_DIR}/src"
CEF_DIR = "${CHROMIUM_DIR}/cef"
DEPOT_TOOLS_DIR="${STAGING_DIR_NATIVE}${datadir}/depot_tools"
S = "${BASE_DIR}/src"
B = "${WORKDIR}/build"

OUT_PATH = "${B}/out/Release_GN_${GN_TARGET_CPU}"
DIST_PATH = "${OUT_PATH}/dist/cef-minimal_${GN_TARGET_CPU}"
CEF_DATA_PATH = "${datadir}/cef"

DEPENDS:append = " curl clang-native gperf-native wayland dbus libcxx compiler-rt libxkbcommon nss nss-native atk at-spi2-atk libdrm pango cairo virtual/egl qemu-native pciutils glib-2.0 pkgconfig-native pulseaudio"

do_sync[depends] += "depot-tools-wam-native:do_populate_sysroot"
do_configure[depends] += "depot-tools-wam-native:do_populate_sysroot"
do_compile[depends] += "depot-tools-wam-native:do_populate_sysroot"

DOWNLOAD_PGO_PROFILES="False"

# Adapted from CEF's tools/gclient_hooks.py
python do_gclient_config() {
  import os
  # Create gclient configuration file.
  gclient_file = os.path.join(d.expand('${BASE_DIR}'), '.gclient')
  # Exclude unnecessary directories. Intentionally written without newlines.
  gclient_spec = \
      "solutions = [{"+\
        "'managed': False,"+\
        "'name': 'src', "+\
        "'url': '" + d.expand('https://${CHROMIUM_URL}') + "', "+\
        "'custom_vars': {"+\
          d.expand("'checkout_pgo_profiles': '${DOWNLOAD_PGO_PROFILES}'") +\
        "}, "+\
        "'custom_deps': {"+\
          "'build': None, "+\
          "'build/scripts/command_wrapper/bin': None, "+\
          "'build/scripts/gsd_generate_index': None, "+\
          "'build/scripts/private/data/reliability': None, "+\
          "'build/scripts/tools/deps2git': None, "+\
          "'build/third_party/lighttpd': None, "+\
          "'commit-queue': None, "+\
          "'depot_tools': None, "+\
          "'src/chrome_frame/tools/test/reference_build/chrome': None, "+\
          "'src/chrome/tools/test/reference_build/chrome_linux': None, "+\
          "'src/chrome/tools/test/reference_build/chrome_mac': None, "+\
          "'src/chrome/tools/test/reference_build/chrome_win': None, "+\
        "}, "+\
        "'deps_file': 'DEPS', "+\
        "'safesync_url': ''"+\
      "}]"

  with open(gclient_file, 'w+', encoding='utf-8') as fp:
    fp.write(gclient_spec)
}
addtask do_gclient_config after do_unpack before do_configure

# Mostly adapted from CEF's tools/automate/automate-git.py
do_sync[network] = "1"
python do_sync() {
  import os
  def cef_run(command_line, working_dir):
    import subprocess
    env = os.environ
    env['PATH'] = d.expand("${DEPOT_TOOLS_DIR}") + os.pathsep + env['PATH']
    env['DEPOT_TOOLS_UPDATE'] = "0"
    env['GCLIENT_PY3'] = "1"

    subprocess.check_output(command_line.split(),
                            cwd=working_dir,
                            env=env,
                            shell=False,
                            stderr=subprocess.STDOUT)

  def cef_apply_patch(name, patches_path=os.path.join(d.expand("${CEF_DIR}"), 'patch', 'patches')):
    patch_file = os.path.join(patches_path, name)
    if not os.path.exists(patch_file + ".patch"):
      # Attempt to apply the patch file.
      patch_tool = os.path.join(d.expand("${CEF_DIR}"), 'tools', 'patcher.py')
      cef_run('%s %s --patch-file "%s" --patch-dir "%s"' %
              ("python3", patch_tool, patch_file, d.expand("${CHROMIUM_DIR}")),
              d.expand("${CHROMIUM_DIR}"))

  def cef_apply_deps_patch():
    """ Patch the Chromium DEPS file before `gclient sync` if necessary. """
    deps_path = os.path.join(d.expand("${CHROMIUM_DIR}"), 'DEPS')
    if os.path.isfile(deps_path):
      cef_apply_patch('DEPS')
    else:
      raise Exception("Path does not exist: DEPS")

  def cef_apply_runhooks_patch():
    """ Patch the Chromium runhooks files before `gclient runhooks` if necessary. """
    cef_apply_patch('runhooks')

  # CEF automation script usually applies
  # applies patches before running sync and
  # runhooks. This is taken directly from the
  # automation script
  def cef_do_sync():
    cef_apply_deps_patch()
    cef_run("gclient sync --reset --nohooks --jobs 16", d.expand("${CHROMIUM_DIR}"))
    cef_apply_runhooks_patch()
    cef_run("gclient runhooks --jobs 16", d.expand("${CHROMIUM_DIR}"))

  cef_do_sync()
}
addtask do_sync after do_gclient_config before do_configure

# gn defaults from CEF wiki, except for use_sysroot
GN_DEFINES = "use_sysroot=false \
              symbol_level=0 \
              is_cfi=false \
              use_thin_lto=false \
"

# Disable GTK and prevent cef from
# building its gtk demos
GN_DEFINES:append = " \
              use_gtk=false \
              cef_use_gtk=false \
"

GN_DEFINES:append = " \
              treat_warnings_as_errors=false \
              is_component_build=false \
              use_cups=false \
              use_kerberos=false \
              use_ozone=true \
              use_xkbcommon=true \
              use_wayland_gbm=true \
              use_gnome_keyring=false \
              enable_remoting=false \
"

# ozone options
GN_DEFINES:append = " \
              use_ozone=true \
              ozone_auto_platforms=false \
              ozone_platform_headless=true \
              ozone_platform_wayland=true \
              ozone_platform_x11=false \
              use_system_minigbm=true \
              use_system_libdrm=true \
              use_system_libwayland=true \
"

GN_DEFINES:append = " \
              dcheck_always_on=false \
              is_debug=false \
              is_official_build=false \
"

GN_DEFINES:append = " \
              use_egl=true \
              use_glib=true \
              use_dri=false \
"

# Disable PGO optimizations
GN_DEFINES:append = " chrome_pgo_phase=0 "

RUNTIME = "llvm"
TOOLCHAIN = "clang"
TOOLCHAIN:class-native = "clang"

BUILD_AR:toolchain-clang = "llvm-ar"
BUILD_CC:toolchain-clang = "clang"
BUILD_CXX:toolchain-clang = "clang++"
BUILD_LD:toolchain-clang = "clang"

COMPATIBLE_MACHINE = "(-)"
COMPATIBLE_MACHINE:aarch64 = "(.*)"
COMPATIBLE_MACHINE:armv6 = "(.*)"
COMPATIBLE_MACHINE:armv7a = "(.*)"
COMPATIBLE_MACHINE:armv7ve = "(.*)"
COMPATIBLE_MACHINE:x86 = "(.*)"
COMPATIBLE_MACHINE:x86-64 = "(.*)"

GN_DEFINES:append = ' \
              use_lld=true \
              use_gold=false \
              gold_path="" \
              is_clang=true \
              clang_use_chrome_plugins=false \
              clang_base_path="${STAGING_BINDIR_NATIVE}" \
              host_toolchain=\"//build/toolchain/cros:host\" \
              use_custom_libcxx_for_host=false \
              cros_host_ar=\"${BUILD_AR}\" \
              cros_host_cc=\"${BUILD_CC}\" \
              cros_host_cxx=\"${BUILD_CXX}\" \
              cros_host_ld=\"${BUILD_CXX}\" \
              cros_host_extra_cppflags=\"${BUILD_CPPFLAGS}\" \
              cros_host_extra_cxxflags=\"${BUILD_CXXFLAGS}\" \
              cros_host_extra_ldflags=\"${BUILD_LDFLAGS}\" \
              custom_toolchain=\"//build/toolchain/cros:target\" \
              use_custom_libcxx=false \
              target_cpu=\"${GN_TARGET_CPU}\" \
              cros_target_ar=\"${AR}\" \
              cros_target_cc=\"${CC}\" \
              cros_target_cxx=\"${CXX}\" \
              cros_target_ld=\"${CXX}\" \
              cros_target_extra_cppflags=\"${CPPFLAGS}\" \
              cros_target_extra_cxxflags=\"${CXXFLAGS}\" \
              cros_target_extra_ldflags=\"${LDFLAGS}\" \
              v8_snapshot_toolchain=\"//build/toolchain/cros:v8_snapshot\" \
              cros_v8_snapshot_ar=\"${BUILD_AR}\" \
              cros_v8_snapshot_cc=\"${BUILD_CC}\" \
              cros_v8_snapshot_cxx=\"${BUILD_CXX}\" \
              cros_v8_snapshot_cppflags=\"${CPPFLAGS}\" \
              cros_v8_snapshot_cxxflags=\"${CXXFLAGS}\" \
              cros_v8_snapshot_ldflags=\"${LDFLAGS}\" \
              use_v8_context_snapshot=false \
'

PACKAGECONFIG ??= "upower use-egl"
PACKAGECONFIG[use-egl] = ",,virtual/egl virtual/libgles2"
PACKAGECONFIG[upower] = ",,,upower"

GN_DEFINES:append = ' \
              ${PACKAGECONFIG_CONFARGS} \
'

do_configure () {
    bbnote "do_configure:"
    bbnote "Base out path: ${B}"

    export DEPOT_TOOLS_UPDATE=0
    export GCLIENT_PY3=1
    export PATH="${DEPOT_TOOLS_DIR}:$PATH"
    export GN_DEFINES="${GN_DEFINES}"

    cd ${S}/cef
    python3 tools/gclient_hook.py --base-out-path ${B} --bypass-sysroot-check
}

do_compile[progress] = "outof:^\[(\d+)/(\d+)\]\s+"
do_compile () {
    if [ ! -f ${OUT_PATH}/build.ninja ]; then
         do_configure
    fi

    export PATH="${DEPOT_TOOLS_DIR}:$PATH"
    export PATH="$PATH:${S}/third_party/ninja"
    ninja ${PARALLEL_MAKE} -C ${OUT_PATH} libcef chrome_sandbox
}

do_install () {
    cd ${S}/cef
    python3 tools/make_distrib.py --output-dir ${OUT_PATH}/dist \
                                  --dist-path-name cef-minimal \
                                  --base-out-path ${B} \
                                  --no-docs \
                                  --no-symbols \
                                  --no-archive \
                                  --ninja-build \
                                  --minimal \
                                  --${GN_TARGET_CPU}-build \
                                  --ozone

    install -d ${D}${CEF_DATA_PATH}

    cp -R --no-dereference --preserve=mode,links -v ${DIST_PATH}/* ${D}${CEF_DATA_PATH}
    # TODO(rzanoni): Follow the wiki instructions to install the sandbox
}

# TODO: fix QA issues, libraries in the wrong location
FILES:${PN} += " \
    ${CEF_DATA_PATH} \
"

INSANE_SKIP:${PN} += "libdir"

PROVIDES:${PN} += "cef"
