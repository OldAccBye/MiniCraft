package de.minicraft.players;

import java.util.HashMap;
import java.util.UUID;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import de.minicraft.config;
import de.minicraft.serverBasics;
import org.bson.Document;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

public class playerApi {
    // Eine Liste an Spieler welche sich mit einem Server verbunden haben
    private static final HashMap<UUID, playerData> playerList = new HashMap<>();

    private static void register(UUID pUUID, Player p) {
        try {
            serverBasics.mongo.collections.get("players").insertOne(new Document("username", p.getName())
                    .append("UUID", pUUID.toString())
                    .append("perm_group", "default")
                    .append("language", "en"));
        } catch (MongoWriteException e) {
            e.printStackTrace();
            p.kickPlayer("[playerApi][addUser] Player could not be saved.");
            return;
        } catch (MongoException e) {
            e.printStackTrace();
            p.kickPlayer("[playerApi][addUser] Something went wrong.");
            return;
        }

        playerData data = new playerData(pUUID, "en");
        data.username = p.getName();
        data.group = "default";
        playerList.put(pUUID, data);
    }

    public static void login(UUID pUUID) {
        Player p = Bukkit.getPlayer(pUUID);
        if (p == null) return;

        // Diese Funktion prüft ob dieser Spieler bereits eingetragen ist und wenn ja entfernt diese Funktion diesen Eintrag
        logout(pUUID);

        Document playerDoc;

        try {
            playerDoc = serverBasics.mongo.collections.get("players").find(Filters.eq("UUID", pUUID.toString())).first();

            if (playerDoc == null) {
                register(pUUID, p);
                return;
            }
        } catch (MongoException e) {
            e.printStackTrace();
            p.kickPlayer("[playerApi][addUser] Something went wrong.");
            return;
        }

        playerData data = new playerData(pUUID, playerDoc.getString("language"));
        data.username = p.getName();
        data.group = playerDoc.getString("perm_group");
        playerList.put(pUUID, data);
    }

    public static playerData get(UUID pUUID) {
        if (!exists(pUUID)) {
            Player p = Bukkit.getPlayer(pUUID);
            if (p != null)
                p.kickPlayer("[ERROR-03] Please contact the support!");
        }

        return playerList.get(pUUID);
    }

    public static void logout(UUID pUUID) { if (exists(pUUID)) playerList.remove(pUUID); }
    public static boolean exists(UUID pUUID) { return playerList.containsKey(pUUID); }

    public static void addAllPerm(UUID pUUID) {
        Player p = Bukkit.getPlayer(pUUID);

        if (p == null) {
            Bukkit.getLogger().severe("[Permissions] player = null");
            return;
        }

        // Setzt dem Spieler die neuen vorgegebenen Permissions aus "permissionsList" und wählt die anhand von der Gruppe des Spielers.
        for (String perm : config.permissionsList.getStringList(get(pUUID).group))
            get(pUUID).permissions.setPermission(perm, true);
    }

    public static void removeAllPerm(UUID pUUID) {
        Player p = Bukkit.getPlayer(pUUID);

        if (p == null) {
            Bukkit.getLogger().severe("[Permissions] player = null");
            return;
        }

        // Entfernt alle Permissions von diesem Spieler indem alle Permissions aus der jetzigen Gruppe auf "false" gesetzt werden.
        for (String perm : config.permissionsList.getStringList(get(pUUID).group))
            get(pUUID).permissions.setPermission(perm, false);
    }
}