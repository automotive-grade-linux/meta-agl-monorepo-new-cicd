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
	@echo "make setup    MACHINE=... FEATURES=a,b [TARGET=...] [EXTRA_FEATURES=...] - create/refresh the bitbake-setup setup"
	@echo "make validate MACHINE=... FEATURES=a,b [TARGET=...] [EXTRA_FEATURES=...] - run the layer QA suite"
	@echo "make build    MACHINE=... FEATURES=a,b [TARGET=...] [EXTRA_FEATURES=...] - build the matrix entry's target"
	@echo "make shell    MACHINE=... FEATURES=a,b [TARGET=...] [EXTRA_FEATURES=...] - interactive shell in the setup (bitbake env sourced)"
	@echo "make lock     MACHINE=... FEATURES=a,b [TARGET=...]                      - show latest upstream commits for one config"
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
	./ci/scripts/shell.sh --machine "$(MACHINE)" --features "$(FEATURES)" --target "$(TARGET)" --extra-features "$(EXTRA_FEATURES)"

# kas/pins.yml is one consolidated file, hand-maintained. `lock` resolves each repo's *latest*
# commit on its declared branch (git ls-remote) for one MACHINE+FEATURES combination and prints
# the bumps so you can hand-copy the ones you want into kas/pins.yml; it never writes the file.
lock:
	python3 ci/scripts/pin-update-helper.py --machine "$(MACHINE)" --features "$(FEATURES)"

# Same resolution for EVERY repo declared across every machine+feature fragment, then updates
# kas/pins.yml in place (targeted text substitution - keeps its comments/grouping). Review the
# resulting diff before committing - this can pull in real upstream breakage, same as any
# dependency bump.
pin-update:
	python3 ci/scripts/pin-update-helper.py

clean:
	rm -rf build/ .bbsetup-*.conf.json
