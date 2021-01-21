package de.minicraft.players;

import java.util.HashMap;
import java.util.UUID;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import de.minicraft.serverbasics;
import org.bson.Document;
import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

public class playerApi {
    // Eine Liste an Spieler welche sich mit einem Server verbunden haben
    private static final HashMap<UUID, playerData> playerList = new HashMap<>();

    public static void createUser(UUID pUUID, Player p) {
        try {
            serverbasics.mongo.collections.get("players").insertOne(new Document("username", p.getName())
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

    public static void addUser(UUID pUUID) {
        Player p = Bukkit.getPlayer(pUUID);
        if (p == null) return;

        // Diese Funktion prüft ob dieser Spieler bereits eingetragen ist und wenn ja entfernt diese Funktion diesen Eintrag
        remove(pUUID);

        Document playerDoc;

        try {
            playerDoc = serverbasics.mongo.collections.get("players").find(Filters.eq("UUID", pUUID.toString())).first();

            if (playerDoc == null) {
                createUser(pUUID, p);
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

    public static playerData getPlayer(UUID pUUID) {
        if (!exists(pUUID)) {
            Player p = Bukkit.getPlayer(pUUID);
            if (p != null)
                p.kickPlayer("[ERROR-03] Please contact the support!");
        }

        return playerList.get(pUUID);
    }

    public static void remove(UUID pUUID) { if (exists(pUUID)) playerList.remove(pUUID); }
    public static boolean exists(UUID pUUID) { return playerList.containsKey(pUUID); }
}