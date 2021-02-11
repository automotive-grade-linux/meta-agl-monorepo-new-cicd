clients_om = ObjectManager {
  Interest {
    type = "client",
    Constraint { "pipewire.access", "=", "restricted" },
  }
}

clients_om:connect("object-added", function (om, client)
  local smack_label = client["global-properties"]["pipewire.sec.label"]

  if smack_label:match("^User::App::.+") then
    -- FIXME: apps can work with less permissions
    client:update_permissions { ["any"] = "all" }
  end
end)

clients_om:activate()
