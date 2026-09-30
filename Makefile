# SPDX-License-Identifier: MIT
.PHONY: help setup validate build shell lock pin-update clean

MACHINE  ?= qemux86-64
FEATURES ?=
TARGET   ?=
EXTRA_FEATURES ?=
SDK_ALLOWED ?= false

# Which layers `make validate`'s yocto-check-layer sub-check validates THIS run (space-separated
# paths, or "all"). Trimmed to just meta-agl-core by default - checking every vendored sublayer
# is slow and most of them aren't what's actively being changed here.
#   make validate CHECK_LAYERS="meta-agl/meta-agl-core meta-agl-demo"
#   make validate CHECK_LAYERS=all
# This variable only selects WHICH curated layers run this time - the curated list itself, plus
# each layer's yocto-check-layer dependencies (--dependency/--additional-layers), live in
# ci/build-matrix.yaml's check_layers: key (see ci/scripts/_matrix.py's load_check_layers()) -
# add a newly-vendored layer's dependency info there, not here.
CHECK_LAYERS ?= meta-agl/meta-agl-core
export CHECK_LAYERS

help:
	@echo "make setup    MACHINE=... FEATURES=a,b [TARGET=...] [EXTRA_FEATURES=...] - checkout kas repos"
	@echo "make validate MACHINE=... FEATURES=a,b [TARGET=...] [EXTRA_FEATURES=...] - run the layer QA suite"
	@echo "make build    MACHINE=... FEATURES=a,b [TARGET=...] [EXTRA_FEATURES=...] - build the matrix entry's target"
	@echo "make shell    MACHINE=... FEATURES=a,b [TARGET=...] [EXTRA_FEATURES=...] - interactive kas-container shell"
	@echo "make lock     MACHINE=... FEATURES=a,b [TARGET=...]                      - resolve latest commits for one config"
	@echo "make pin-update                                                          - resolve+write EVERY repo's latest tip into kas/pins.yml"
	@echo "make clean                                                               - remove build/ output"
	@echo ""
	@echo "MACHINE/FEATURES must match an entry in ci/build-matrix.yaml. TARGET is only needed"
	@echo "when several images share the same machine+features (e.g. the agl-demo group) - see"
	@echo "the error message for the candidate list. EXTRA_FEATURES adds local-only extras on"
	@echo "top of a matched entry (e.g. EXTRA_FEATURES=agl-devel for passwordless login) -"
	@echo "never matrix-curated, never used in CI."
	@echo ""
	@echo "AGL_FLOATING=1 make build/setup/shell ... swaps kas/pins.yml for kas/floating.yml"
	@echo "(no commit overrides - every repo floats to the tip of its declared branch). Local-only."
	@echo ""
	@echo "CHECK_LAYERS=\"meta-agl/... meta-agl-demo\" or CHECK_LAYERS=all make validate ... expands"
	@echo "which layers yocto-check-layer validates beyond the default (meta-agl-core only) -"
	@echo "the curated set + each layer's dependencies live in ci/build-matrix.yaml's check_layers:."

setup:
	./ci/scripts/setup.sh --machine "$(MACHINE)" --features "$(FEATURES)" --target "$(TARGET)" --extra-features "$(EXTRA_FEATURES)"

validate: setup
	./ci/scripts/validate.sh --machine "$(MACHINE)" --features "$(FEATURES)" --target "$(TARGET)" --extra-features "$(EXTRA_FEATURES)"

build: setup
	./ci/scripts/build.sh --machine "$(MACHINE)" --features "$(FEATURES)" --target "$(TARGET)" --extra-features "$(EXTRA_FEATURES)" --sdk-allowed "$(SDK_ALLOWED)"

shell: setup
	. ci/scripts/_kas_runtime_args.sh && \
	KAS_WORK_DIR="$(CURDIR)" kas-container $$(kas_runtime_args) shell \
	    "$$(python3 ci/scripts/_compose_kasfiles.py --machine "$(MACHINE)" --features "$(FEATURES)" --target "$(TARGET)" --extra-features "$(EXTRA_FEATURES)")$$(kas_extra_includes)"

# kas/pins.yml is one consolidated file, hand-maintained (not auto-discovered by kas -
# it's just another entry colon-joined at the end of every build). This target resolves each
# repo's *latest* commit on its declared branch so you can review and hand-copy any bumps you
# want into kas/pins.yml; it does not edit that file itself (kas has no "write pins into an
# arbitrary existing file" mode - only auto-naming a lockfile after the first input file, which
# would silently shadow pins.yml on every future build if used here, so we route around it via
# a disposable scratch file instead).
lock:
	@mkdir -p build
	@printf 'header:\n  version: 23\n' > build/.lock-scratch.yml
	KAS_WORK_DIR="$(CURDIR)" kas-container lock --update --sort \
	    "build/.lock-scratch.yml:$$(python3 ci/scripts/_compose_kasfiles.py --machine "$(MACHINE)" --features "$(FEATURES)" --target "$(TARGET)")"
	@echo ""
	@echo "Freshly resolved commits (build/.lock-scratch.lock.yml):"
	@cat build/.lock-scratch.lock.yml
	@echo ""
	@echo "Hand-copy any repo(s) you want to bump into kas/pins.yml, then:"
	@echo "  rm -f build/.lock-scratch.yml build/.lock-scratch.lock.yml"


# Resolves EVERY repo declared across every machine+feature fragment to its current branch-tip
# commit (against kas/floating.yml, i.e. unpinned) in one pass, then updates kas/pins.yml
# in place via ci/scripts/pin-update-helper.py (targeted text substitution - keeps pins.yml's
# existing comments/grouping, doesn't need a full YAML round-trip). Review the resulting diff
# before committing - this can pull in real upstream breakage, same as any dependency bump.
pin-update:
	@mkdir -p build
	@printf 'header:\n  version: 23\n' > build/.pin-update-scratch.yml
	KAS_WORK_DIR="$(CURDIR)" kas-container lock --update --sort \
	    "build/.pin-update-scratch.yml:kas/base.yml:$$(find kas/machine kas/feature -name '*.yml' | sort | paste -sd: -):kas/floating.yml"
	python3 ci/scripts/pin-update-helper.py build/.pin-update-scratch.lock.yml
	@rm -f build/.pin-update-scratch.yml build/.pin-update-scratch.lock.yml

clean:
	rm -rf build/
