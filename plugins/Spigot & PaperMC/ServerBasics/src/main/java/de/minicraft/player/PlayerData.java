package de.minicraft.player;

import org.bukkit.permissions.PermissionAttachment;

public class PlayerData {
    public String username, group;
    public PermissionAttachment permissions;

    PlayerData(PermissionAttachment permAttachment) { this.permissions = permAttachment; }
}