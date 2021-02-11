-- ["<factory-name regex>"] = "<library-name>"
--
-- used to find spa factory names. It maps a spa factory name
-- regular expression to a library name that should contain that factory.
--
spa_libs = {
  ["api.alsa.*"] = "alsa/libspa-alsa",
  ["api.v4l2.*"] = "v4l2/libspa-v4l2",
  ["api.bluez5.*"] = "bluez5/libspa-bluez5",
}
