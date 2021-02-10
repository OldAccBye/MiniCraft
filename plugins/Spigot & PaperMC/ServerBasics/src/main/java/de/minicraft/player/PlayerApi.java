package de.minicraft.player;

import java.util.UUID;

import de.minicraft.Configs;
import de.minicraft.ServerBasics;

import org.bson.Document;
import org.bukkit.entity.Player;

public class PlayerApi {
    public static Document preLogin(Player p, String group) {
        PlayerData data = new PlayerData(p.addAttachment(ServerBasics.plugin));
        data.username = p.getName();
        data.group = group;
        ServerBasics.playerList.put(p.getUniqueId(), data);
        return new Document("boolean", true);
    }

    public static PlayerData get(UUID pUUID) { return ServerBasics.playerList.get(pUUID); }

    public static void logout(UUID pUUID) { ServerBasics.playerList.remove(pUUID); }

    public static void addAllPerm(Player p) {
        PlayerData pData = get(p.getUniqueId());
        if (pData == null) {
            p.kickPlayer("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden.");
            return;
        }

        // Setzt dem Spieler die neuen vorgegebenen Permissions aus "permissionsList" und wählt die anhand von der Gruppe des Spielers.
        for (String perm : Configs.permissionsList.getStringList(pData.group))
            pData.permissions.setPermission(perm, true);
    }

    public static void removeAllPerm(Player p) {
        PlayerData pData = get(p.getUniqueId());
        if (pData == null) {
            p.kickPlayer("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden.");
            return;
        }

        // Entfernt alle Permissions von diesem Spieler indem alle Permissions aus der jetzigen Gruppe auf "false" gesetzt werden.
        for (String perm : Configs.permissionsList.getStringList(pData.group))
            pData.permissions.setPermission(perm, false);
    }
}