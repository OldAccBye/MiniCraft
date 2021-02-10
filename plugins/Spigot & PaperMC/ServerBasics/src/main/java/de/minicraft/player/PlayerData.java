package de.minicraft.player;

import org.bukkit.permissions.PermissionAttachment;

public class PlayerData {
    public String username, group;
    public Long blockBreakEventTimestamp;
    public Integer blockBreakEventCounter;
    public PermissionAttachment permissions;

    PlayerData(PermissionAttachment permAttachment) { this.permissions = permAttachment; }
}