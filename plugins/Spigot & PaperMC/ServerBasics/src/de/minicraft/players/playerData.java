package de.minicraft.players;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionAttachment;

import de.minicraft.serverbasics;

public class playerData {
    public String username;
    public String group;
    public PermissionAttachment permissions;

    playerData(UUID pUUID) {
        Bukkit.getLogger().info("[PlayerApi] Constructor");
        permissions = Objects.requireNonNull(Bukkit.getPlayer(pUUID)).addAttachment(serverbasics.plugin);
    }
}