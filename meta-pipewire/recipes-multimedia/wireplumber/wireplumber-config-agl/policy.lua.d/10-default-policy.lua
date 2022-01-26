-- Policy config file --

policy_config = {}

policy_config.endpoints = {
  -- [endpoint name] = { endpoint properties }
  ["endpoint.capture"] = {
    ["media.class"] = "Audio/Source",
    ["role"] = "Capture",
  },
  ["endpoint.multimedia"] = {
    ["media.class"] = "Audio/Sink",
    ["role"] = "Multimedia",
  },
  ["endpoint.speech_low"] = {
    ["media.class"] = "Audio/Sink",
    ["role"] = "Speech-Low",
  },
  ["endpoint.custom_low"] = {
    ["media.class"] = "Audio/Sink",
    ["role"] = "Custom-Low",
  },
  ["endpoint.navigation"] = {
    ["media.class"] = "Audio/Sink",
    ["role"] = "Navigation",
  },
  ["endpoint.speech_high"] = {
    ["media.class"] = "Audio/Sink",
    ["role"] = "Speech-High",
  },
  ["endpoint.custom_high"] = {
    ["media.class"] = "Audio/Sink",
    ["role"] = "Custom-High",
  },
  ["endpoint.communication"] = {
    ["media.class"] = "Audio/Sink",
    ["role"] = "Communication",
  },
  ["endpoint.emergency"] = {
    ["media.class"] = "Audio/Sink",
    ["role"] = "Emergency",
  },
}

policy_config.policy = {
  ["move"] = false,  -- moves session items when metadata target.node changes
  ["follow"] = true, -- moves session items to the default device when it has changed

  -- Set to 'true' to disable channel splitting & merging on nodes and enable
  -- passthrough of audio in the same format as the format of the device.
  -- Note that this breaks JACK support; it is generally not recommended
  ["audio.no-dsp"] = false,

  -- how much to lower the volume of lower priority streams when ducking
  -- note that this is a linear volume modifier (not cubic as in pulseaudio)
  ["duck.level"] = 0.2,

  ["roles"] = {
    ["Capture"] = {
      ["alias"] = { "Multimedia", "Music", "Voice", "Capture" },
      ["priority"] = 25,
      ["action.default"] = "cork",
      ["action.Capture"] = "mix",
      ["media.class"] = "Audio/Source",
    },
    ["Multimedia"] = {
      ["alias"] = { "Movie", "Music", "Game" },
      ["priority"] = 25,
      ["action.default"] = "cork",
    },
    ["Speech-Low"] = {
      ["priority"] = 30,
      ["action.default"] = "cork",
      ["action.Speech-Low"] = "mix",
    },
    ["Custom-Low"] = {
      ["priority"] = 35,
      ["action.default"] = "cork",
      ["action.Custom-Low"] = "mix",
    },
    ["Navigation"] = {
      ["priority"] = 50,
      ["action.default"] = "duck",
      ["action.Navigation"] = "mix",
    },
    ["Speech-High"] = {
      ["priority"] = 60,
      ["action.default"] = "cork",
      ["action.Speech-High"] = "mix",
    },
    ["Custom-High"] = {
      ["priority"] = 65,
      ["action.default"] = "cork",
      ["action.Custom-High"] = "mix",
    },
    ["Communication"] = {
      ["priority"] = 75,
      ["action.default"] = "cork",
      ["action.Communication"] = "mix",
    },
    ["Emergency"] = {
      ["alias"] = { "Alert" },
      ["priority"] = 99,
      ["action.default"] = "cork",
      ["action.Emergency"] = "mix",
    },
  },
}

-- Session item factories, building blocks for the session management graph
-- Do not disable these unless you really know what you are doing
load_module("si-node")
load_module("si-audio-adapter")
load_module("si-standard-link")
load_module("si-audio-endpoint")

-- API to access default nodes from scripts
load_module("default-nodes-api")

-- API to access mixer controls, needed for volume ducking
load_module("mixer-api")

-- Create endpoints statically at startup
load_script("static-endpoints.lua", policy_config.endpoints)

-- Create session items for nodes that appear in the graph
load_script("create-item.lua", policy_config.policy)

-- Link nodes to each other to make media flow in the graph
load_script("policy-node.lua", policy_config.policy)

-- Link client nodes with endpoints to make media flow in the graph
load_script("policy-endpoint-client.lua", policy_config.policy)
load_script("policy-endpoint-client-links.lua", policy_config.policy)

-- Link endpoints with device nodes to make media flow in the graph
load_script("policy-endpoint-device.lua", policy_config.policy)
