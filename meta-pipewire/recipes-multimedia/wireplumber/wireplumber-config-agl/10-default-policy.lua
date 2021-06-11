-- Default policy config file --

default_policy = {}

default_policy.endpoints = {
  -- [endpoint name] = { endpoint properties }

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

default_policy.policy = {
  ["move"] = false,  -- moves session items when metadata target.node changes
  ["follow"] = true, -- moves session items to the default device when it has changed

  -- how much to lower the volume of lower priority streams when ducking
  -- note that this is a linear volume modifier (not cubic as in the mixer)
  ["duck.level"] = 0.2,

  ["roles"] = {
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

function default_policy.enable()
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
  load_script("static-endpoints.lua", default_policy.endpoints)

  -- Create session items for nodes that appear in the graph
  load_script("create-item.lua")

  -- Link nodes to each other to make media flow in the graph
  load_script("policy-node.lua", default_policy.policy)

  -- Link client nodes with endpoints to make media flow in the graph
  load_script("policy-endpoint-client.lua", default_policy.policy)
  load_script("policy-endpoint-client-links.lua", default_policy.policy)

  -- Link endpoints with device nodes to make media flow in the graph
  load_script("policy-endpoint-device.lua", default_policy.policy)
end
