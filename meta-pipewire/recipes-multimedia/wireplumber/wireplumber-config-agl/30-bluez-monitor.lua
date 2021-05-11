-- Bluez monitor config file --

bluez_monitor = {}

bluez_monitor.properties = {
  -- Enable mSBC support, disabled by default. Be aware that
  -- mSBC is not expected to work on all headset + adapter combinations.
  -- This can be overloaded for a specific device and native backend
  -- in rules section.
  --["bluez5.msbc-support"] = false,

  --["bluez5.sbc-xq-support"] = true,

  -- Enabled headset roles (default: [ hsp_hs hfp_ag ]), this
  -- property only applies to native backend. Currently some headsets
  -- (Sony WH-1000XM3) are not working with both hsp_ag and hfp_ag
  -- enabled, disable either hsp_ag or hfp_ag to work around it.
  --
  -- Supported headset roles: hsp_hs (HSP Headset),
  --                          hsp_ag (HSP Audio Gateway),
  --                          hfp_hf (HFP Hands-Free),
  --                          hfp_ag (HFP Audio Gateway)
  ["bluez5.headset-roles"] = "[ hsp_hs hsp_ag hfp_hf hfp_ag ]",

  -- Enabled A2DP codecs (default: all).
  --["bluez5.codecs"] = "[ sbc aac ldac aptx aptx_hd ]",

  -- Properties for the A2DP codec configuration
  --["bluez5.default.rate"] = 48000,
  --["bluez5.default.channels"] = 2,
}

bluez_monitor.rules = {
  -- An array of matches/actions to evaluate.
  {
    -- Rules for matching a device or node. It is an array of
    -- properties that all need to match the regexp. If any of the
    -- matches work, the actions are executed for the object.
    matches = {
      {
        -- This matches all cards.
        { "device.name", "matches", "bluez_card.*" },
      },
    },
    -- Apply properties on the matched object.
    apply_properties = {
      -- Auto-connect device profiles on start up or when only partial
      -- profiles have connected. Disabled by default if the property
      -- is not specified.
      --["bluez5.auto-connect"] = "[ hfp_hf hsp_hs a2dp_sink hfp_ag hsp_ag a2dp_source ]",
      ["bluez5.auto-connect"]  = "[ hfp_hf hsp_hs a2dp_sink ]",

      -- Overload mSBC support for native backend and a specific device.
      --["bluez5.msbc-support"] = false,

      -- Hardware volume control (default: [ hfp_ag hsp_ag a2dp_source ])
      --["bluez5.hw-volume"] = "[ hfp_hf hsp_hs a2dp_sink hfp_ag hsp_ag a2dp_source ]",

      -- LDAC encoding quality
      -- Available values: auto (Adaptive Bitrate, default)
      --                   hq   (High Quality, 990/909kbps)
      --                   sq   (Standard Quality, 660/606kbps)
      --                   mq   (Mobile use Quality, 330/303kbps)
      --["bluez5.a2dp.ldac.quality"] = "auto",

      -- AAC variable bitrate mode
      -- Available values: 0 (cbr, default), 1-5 (quality level)
      --["bluez5.a2dp.aac.bitratemode"] = 0,

      -- Profile connected first
      -- Available values: a2dp-sink (default), headset-head-unit
      --["device.profile"] = "a2dp-sink",
    },
  },
  {
    -- Make output hsp/hfp stream nodes go through the Communication endpoint
    -- Unfortunately we cannot match on "media.class" because this property
    -- is not known before the node is created
    matches = {
      {
        { "api.bluez5.profile", "equals", "headset-audio-gateway" },
        { "factory.name", "matches", "*source*" },
      },
    },
    apply_properties = {
      ["media.role"]  = "Communication",
    },
  },
  {
    -- Make output a2dp stream nodes go through the Multimedia endpoint
    -- Unfortunately we cannot match on "media.class" because this property
    -- is not known before the node is created
    matches = {
      {
        { "api.bluez5.profile", "equals", "a2dp-source" },
      },
    },
    apply_properties = {
      ["media.role"]  = "Multimedia",
    },
  },
}

function bluez_monitor.enable()
  load_monitor("bluez", {
    properties = bluez_monitor.properties,
    rules = bluez_monitor.rules,
  })
end
