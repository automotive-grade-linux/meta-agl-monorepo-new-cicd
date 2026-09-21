-- WirePlumber
--
-- This script mutes all ALSA sinks when the "suspend.playback" metadata
-- key is set to 1; compliments pipewire-ic-ipc and the respective support
-- for handling "suspend.playback" in the policy scripts
--
-- Copyright © 2021 Collabora Ltd.
--    @author George Kiagiadakis <george.kiagiadakis@collabora.com>
--
-- SPDX-License-Identifier: MIT

mixer_api = Plugin.find("mixer-api")

nodes_om = ObjectManager {
  Interest { type = "node",
    Constraint { "media.class", "matches", "Audio/Sink" },
    Constraint { "object.path", "matches", "alsa:pcm:*" },
  },
}

metadata_om = ObjectManager {
  Interest { type = "metadata",
    Constraint { "metadata.name", "=", "default" },
  }
}

metadata_om:connect("object-added", function (om, metadata)
  metadata:connect("changed", function (m, subject, key, t, value)
    if key == "suspend.playback" then
      local suspended = (value == "1")

      Log.info(string.format("%s ALSA nodes for IC sound",
                             suspended and "muting" or "unmuting"))

      for n in nodes_om:iterate() do
        mixer_api:call("set-volume", n["bound-id"], {
          ["mute"] = suspended,
        })
      end
    end
  end)
end)

nodes_om:activate()
metadata_om:activate()
