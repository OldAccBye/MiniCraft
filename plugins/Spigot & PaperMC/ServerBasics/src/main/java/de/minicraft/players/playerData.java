package de.minicraft.players;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionAttachment;

import de.minicraft.serverbasics;

public class playerData {
    public UUID pUUID;
    public String username;
    public String language;
    public String group;
    public PermissionAttachment permissions;

    playerData(UUID pUUID, String language) {
        this.pUUID = pUUID;
        this.language = language;
        this.permissions = Objects.requireNonNull(Bukkit.getPlayer(pUUID)).addAttachment(serverbasics.plugin);
    }

    public void saveAll() {
        Bukkit.getLogger().info("Player " + this.username + " saved!");
    }
}