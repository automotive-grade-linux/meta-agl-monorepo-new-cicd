# ponytail: grpc's upstream v1.80.x branch was force-pushed past our pinned SRCREV
# (tag v1.80.0 still resolves to it, branch containment check doesn't). nobranch=1
# skips that check; bump BRANCH/SRCREV together if meta-openembedded moves on.
SRC_URI:remove = "gitsm://github.com/grpc/grpc.git;protocol=https;branch=${BRANCH};tag=v${PV}"
SRC_URI:append = " gitsm://github.com/grpc/grpc.git;protocol=https;nobranch=1;tag=v${PV}"
