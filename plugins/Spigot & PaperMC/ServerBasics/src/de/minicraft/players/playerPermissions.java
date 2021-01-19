package de.minicraft.players;

import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.minicraft.config;

public class playerPermissions {
    public static void add(UUID pUUID) {
        Player p = Bukkit.getPlayer(pUUID);

        if (p == null) {
            Bukkit.getLogger().severe("[Permissions] player = null");
            return;
        }

        /*
         * Setzt dem Spieler die neuen vorgegebenen Permissions aus "permissionsList" und wählt die anhand
         * von der Gruppe des Spielers.
         */
        for (String perm : config.permissionsList.getStringList(Objects.requireNonNull(playerApi.getPlayer(pUUID)).group))
            Objects.requireNonNull(playerApi.getPlayer(pUUID)).permissions.setPermission(perm, true);
    }

    public static void removeAll(UUID pUUID) {
        Player p = Bukkit.getPlayer(pUUID);

        if (p == null) {
            Bukkit.getLogger().severe("[Permissions] player = null");
            return;
        }

        Bukkit.getLogger().warning("[Permissions] RemoveAll");

        for (String perm : config.permissionsList.getStringList(Objects.requireNonNull(playerApi.getPlayer(pUUID)).group))
            playerApi.getPlayer(pUUID).permissions.setPermission(perm, false);
    }
}