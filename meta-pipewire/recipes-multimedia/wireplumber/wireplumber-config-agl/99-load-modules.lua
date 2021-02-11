-- Enable local & bluetooth audio devices
alsa_monitor.enable()
bluez_monitor.enable()

-- Load policy
default_policy.enable()

-- Implements storing metadata about objects in RAM
load_module("metadata")

-- Keeps track of the "default" sources and sinks
load_module("default-nodes", {
  -- do not store runtime user changes in $HOME
  ["use-persistent-storage"] = false,
})
load_module("default-nodes-api")

-- Automatically suspends idle nodes after 3 seconds
load_script("suspend-node.lua")

-- Automatically sets device profiles to 'On'
load_module("device-activation")
