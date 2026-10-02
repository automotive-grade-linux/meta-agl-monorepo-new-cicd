#!/usr/bin/env python3
# SPDX-License-Identifier: MIT
"""Diff two `bitbake -e` dumps (kas vs bitbake-setup builds of the same target) after
normalising the path differences that are inherent to the two layouts (checkout dir, TOPDIR).

Usage: _diff_bbenv.py KAS_ENV.txt BBSETUP_ENV.txt SETUP_NAME [--all]
Prints variables whose final (expanded) value differs, minus IGNORE (varies per run/host) and
KNOWN (understood, intentional differences). Exit 1 if any other variable differs."""
import re
import sys

IGNORE = {"BB_ORIGENV", "DATE", "TIME", "DATETIME", "BUILDCFG_HEADER", "BUILDCFG_VARS", "BB_CURRENTTASK",
          "BB_UI_HANDLERS", "BB_SERVER_TIMEOUT", "BB_HASHSERVE", "PWD", "OLDPWD", "BB_ENV_PASSTHROUGH_ADDITIONS",
          "BB_ENV_PASSTHROUGH", "PATH", "BB_LOGCONFIG", "FILE", "BBINCLUDED", "BBPATH", "BB_HASHSERVE_DB_DIR",
          "OE_FRAGMENTS", "BBLAYERS", "BB_CONSOLELOG", "USER", "HOME", "SHELL", "TERM", "BUILDHISTORY_DIR",
          "ORIGENV", "BB_TASKHASH", "BB_PRESERVE_ENV", "BB_NUMBER_THREADS", "BBSERVER", "BB_SERVER_TIMEOUT",
          "SDKPATH", "BUILDNAME"}


# Understood, intentional differences between the two setups:
KNOWN = {
    "BBFILES", "BBFILE_COLLECTIONS",                    # + the (recipe-less) agl-setup fragments layer
    "BBFILE_PATTERN_IGNORE_EMPTY_agl-setup", "BBFILE_PATTERN_agl-setup", "BBFILE_PRIORITY_agl-setup",
    "LAYERSERIES_COMPAT_agl-setup",
    "LCONF_VERSION",       # kas: 7 (its header "6" auto-migrated); bitbake-setup: unset, sanity is fine with that
    "BBMULTICONFIG",       # kas writes `BBMULTICONFIG ?= ""`
    "GIT_PROXY_COMMAND", "NO_PROXY",                    # kas's own default env passthrough
    "COMBINED_FEATURES",   # set union, iteration order varies between runs
    "LOGFIFO", "PID", "LOGNAME", "XAUTHORITY",           # per-run / container user
}


def parse(path, norm):
    out, comment = {}, []
    for line in open(path, errors="replace"):
        m = re.match(r'^(?:export\s+)?([A-Za-z0-9_${}:.+\-]+)="(.*)"$', line.rstrip("\n"))
        if m and not line.startswith("#"):
            out[m.group(1)] = norm(m.group(2))
    return out


def main():
    kas, bbs, setup = sys.argv[1:4]
    show_all = "--all" in sys.argv
    layers = f"/work/build/{setup}/layers/"
    topdir = f"/work/build/{setup}/build"

    ts = re.compile(r"\b20\d{12}\b")

    def norm_kas(v):   # kas-container: repo at /work, build dir at /build
        v = v.replace("/build/../work/", "/work/").replace("/work/build/", "<TOP>/")
        v = v.replace("/build/tmp", "<TOP>/tmp").replace("/build/sstate", "<TOP>/sstate")
        v = re.sub(r"(?<![\w/<>])/build(?=/|\b)", "<TOP>", v)
        return ts.sub("<TS>", v)

    def norm_bbs(v):
        v = v.replace(layers, "/work/").replace(topdir + "/", "<TOP>/").replace(topdir, "<TOP>")
        v = v.replace("/work/build/", "<TOP>/")   # shared build/site.conf dirs (DL_DIR, SSTATE_DIR)
        return ts.sub("<TS>", v)

    a, b = parse(kas, norm_kas), parse(bbs, norm_bbs)
    diffs = []
    for k in sorted(set(a) | set(b)):
        if k in IGNORE or k in KNOWN or k.startswith("BB_") and not show_all:
            continue
        if a.get(k) != b.get(k):
            diffs.append(k)
    for k in diffs[: (None if show_all else 60)]:
        print(f"## {k}\n  kas: {str(a.get(k))[:300]}\n  bbs: {str(b.get(k))[:300]}")
    print(f"{len(diffs)} differing variables ({len(a)} kas / {len(b)} bbsetup)")
    sys.exit(1 if diffs else 0)


if __name__ == "__main__":
    main()
