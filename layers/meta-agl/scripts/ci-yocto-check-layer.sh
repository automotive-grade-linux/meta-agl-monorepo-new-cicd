#!/bin/bash
set -e

pushd .

pushd meta-agl-core
./scripts/run-yocto-check-layer.sh
popd

pushd meta-netboot
./scripts/run-yocto-check-layer.sh
popd

pushd meta-pipewire
./scripts/run-yocto-check-layer.sh
popd

pushd meta-app-framework
./scripts/run-yocto-check-layer.sh
popd

popd