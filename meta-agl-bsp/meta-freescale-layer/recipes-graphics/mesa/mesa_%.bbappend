PACKAGECONFIG:append:etnaviv = " etnaviv kmsro"

# Needed to allow using etnaviv with the NXP BSP as we do for
# the i.MX8MQ EVK.
RRECOMMENDS:${PN}-megadriver:append:etnaviv = " libdrm-etnaviv"
