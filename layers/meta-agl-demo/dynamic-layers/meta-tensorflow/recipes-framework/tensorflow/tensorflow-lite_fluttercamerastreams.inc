# This is a backport from tip of tree meta-tensorflow (10/2025)
# It can be removed if the layer is uprev'd

do_install:append () {

    # install the headers also
    cd ${S}
    for header in `find ./tensorflow/lite -type f -name *.h`; do
        install -D -m 0644 $header ${D}${includedir}/$header
    done

    for header in `find ./tensorflow/compiler/mlir -type f -name *.h`; do
        install -D -m 0644 $header ${D}${includedir}/$header
    done

    for header in `find ./tensorflow/core/public -type f -name *.h`; do
        install -D -m 0644 $header ${D}${includedir}/$header
    done
    cd -

    cd ${WORKDIR}/bazel/output_base/external/flatbuffers/include
    for header in `find ./flatbuffers -type f`; do
        install -D -m 0644 $header ${D}${includedir}/$header
    done
    cd -

    # header installation done

}
