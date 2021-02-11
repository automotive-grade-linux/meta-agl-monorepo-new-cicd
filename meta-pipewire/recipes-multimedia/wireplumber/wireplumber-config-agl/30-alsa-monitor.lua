-- ALSA monitor config file --

alsa_monitor = {}

alsa_monitor.properties = {
  ["alsa.jack-device"] = false,
  ["alsa.reserve"] = false,
}

alsa_monitor.rules = {
  -- disable ACP (PulseAudio-like profiles)
  {
    matches = {
      {
        { "device.name", "matches", "alsa_card.*" },
      },
    },
    apply_properties = {
      ["api.alsa.use-acp"] = false,
    },
  },

  --
  -- Bump priority of well-known output devices
  -- Higher priority means it gets selected as the default if it's present
  --

  -- USB card
  {
    matches = {
      {
        { "node.name", "matches", "alsa_output.*" },
        { "api.alsa.card.driver", "=", "USB-Audio" },
      },
    },
    apply_properties = {
      ["priority.driver"]        = 1300,
      ["priority.session"]       = 1300,
    }
  },

  -- fiberdyne amp
  {
    matches = {
      {
        { "node.name", "matches", "alsa_output.*" },
        { "api.alsa.card.id", "=", "ep016ch" },
      },
    },
    apply_properties = {
      ["priority.driver"]        = 1200,
      ["priority.session"]       = 1200,
    }
  },

  -- well-known internal devices
  {
    matches = {
      {
        -- ak4613
        { "node.name", "matches", "alsa_output.*" },
        { "api.alsa.card.id", "=", "ak4613" },
        { "api.alsa.pcm.device", "=", "0" },
      },
      {
        -- dra7xx
        { "node.name", "matches", "alsa_output.*" },
        { "api.alsa.card.id", "=", "DRA7xx-EVM" },
      },
      {
        -- imx8mq
        { "node.name", "matches", "alsa_output.*" },
        { "api.alsa.card.id", "=", "wm8524audio" },
      },
      {
        -- rcarsound
        { "node.name", "matches", "alsa_output.*" },
        { "api.alsa.card.id", "=", "rcarsound" },
        { "api.alsa.pcm.device", "=", "0" },
      },
      {
        -- rpi3
        { "node.name", "matches", "alsa_output.*" },
        { "api.alsa.pcm.name", "=", "bcm2835 ALSA" },
      },
    },
    apply_properties = {
      ["priority.driver"]        = 1100,
      ["priority.session"]       = 1100,
    }
  },

  --
  -- Same for input devices
  --

  -- USB card
  {
    matches = {
      {
        { "node.name", "matches", "alsa_input.*" },
        { "api.alsa.card.driver", "=", "USB-Audio" },
      },
    },
    apply_properties = {
      ["priority.driver"]        = 2300,
      ["priority.session"]       = 2300,
    }
  },

  -- microchip mic
  {
    matches = {
      {
        { "node.name", "matches", "alsa_input.*" },
        { "api.alsa.card.id", "=", "ep811ch" },
      },
    },
    apply_properties = {
      ["priority.driver"]        = 2200,
      ["priority.session"]       = 2200,
    }
  },

  -- well-known internal devices
  {
    matches = {
      {
        -- ak4613
        { "node.name", "matches", "alsa_input.*" },
        { "api.alsa.card.id", "=", "ak4613" },
      },
      {
        -- dra7xx
        { "node.name", "matches", "alsa_input.*" },
        { "api.alsa.card.id", "=", "DRA7xx-EVM" },
      },
      {
        -- imx8mq
        { "node.name", "matches", "alsa_input.*" },
        { "api.alsa.card.id", "=", "wm8524audio" },
      },
      {
        -- rcarsound
        { "node.name", "matches", "alsa_input.*" },
        { "api.alsa.card.id", "=", "rcarsound" },
      },
    },
    apply_properties = {
      ["priority.driver"]        = 2100,
      ["priority.session"]       = 2100,
    }
  },
}

function alsa_monitor.enable()
  load_monitor("alsa", {
    properties = alsa_monitor.properties,
    rules = alsa_monitor.rules,
  })
end
