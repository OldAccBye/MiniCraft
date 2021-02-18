package de.minicraft.player;

import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import de.minicraft.BungeeSystem;
import de.minicraft.Configs;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerApi {
    public static Document login(ProxiedPlayer p) {
        // Daten aus der Datenbank werden hier zwischen gespeichert
        Document playerDoc = getPlayerData(p, "player");
        PlayerData data = new PlayerData();

        if (playerDoc.containsKey("status")) {
            if (playerDoc.getString("status").equals("error")) return playerDoc;

            data.username = p.getName();
            for (String permissionKey : Configs.permissionsList.getKeys()) {
                data.permissions.addAll(Configs.permissionsList.getStringList(permissionKey));
                if (permissionKey.equals(data.group)) break;
            }
            BungeeSystem.playerList.put(p.getUniqueId(), data);

            return playerDoc; // Gibt ein status->success zurück
        }

        data.username = p.getName();
        data.group = playerDoc.getString("group");
        for (String permissionKey : Configs.permissionsList.getKeys()) {
            data.permissions.addAll(Configs.permissionsList.getStringList(permissionKey));
            if (permissionKey.equals(data.group)) break;
        }
        p.sendMessage(new TextComponent(data.permissions.toString()));
        data.banned = playerDoc.getBoolean("banned");
        data.banSinceTimestamp = playerDoc.getLong("banSinceTimestamp");
        data.banExpiresTimestamp = playerDoc.getLong("banExpiresTimestamp");
        data.banReason = playerDoc.getString("banReason");
        data.bannedFrom = playerDoc.getString("bannedFrom");
        data.friends = playerDoc.getList("friends", String.class);
        data.cookies = playerDoc.getInteger("cookies");
        BungeeSystem.playerList.put(p.getUniqueId(), data);

        return new Document("status", "success");
    }

    public static Document getPlayerData(ProxiedPlayer p, String collection) {
        Document playerDoc = new Document();

        try {
            switch (collection) {
                case "player" -> {
                    playerDoc = BungeeSystem.mongo.collections.get("player").find(Filters.eq("UUID", p.getUniqueId().toString())).first();

                    if (playerDoc == null) {
                        BungeeSystem.mongo.collections.get("player").insertOne(new Document("username", p.getName())
                                .append("UUID", p.getUniqueId().toString())
                                .append("cookies", 0)
                                .append("group", "default")
                                .append("friends", new ArrayList<>())
                                .append("securitycode", "null")
                                .append("banned", false)
                                .append("banSinceTimestamp", 0)
                                .append("banExpiresTimestamp", 0)
                                .append("banReason", "")
                                .append("bannedFrom", ""));
                        return new Document("status", "success");
                    }
                }
                case "ffa" -> {
                    playerDoc = BungeeSystem.mongo.collections.get("ffa").find(Filters.eq("UUID", p.getUniqueId().toString())).first();

                    if (playerDoc == null) {
                        BungeeSystem.mongo.collections.get("ffa").insertOne(new Document("UUID", p.getUniqueId().toString())
                                .append("kills", 0)
                                .append("deaths", 0));
                        return new Document("status", "success");
                    }
                }
                case "gtc" -> {
                    playerDoc = BungeeSystem.mongo.collections.get("gtc").find(Filters.eq("UUID", p.getUniqueId().toString())).first();

                    if (playerDoc == null) {
                        BungeeSystem.mongo.collections.get("gtc").insertOne(new Document("UUID", p.getUniqueId().toString()).append("won", 0));
                        return new Document("status", "success");
                    }
                }
            }
        } catch (MongoException e) {
            e.printStackTrace();
            return new Document("status", "error").append("reason", "[PlayerApi->getPlayerData] Irgendetwas ist schief gelaufen.");
        }

        return playerDoc;
    }

    public static void saveAll(UUID pUUID) {
        try {
            Document found = BungeeSystem.mongo.collections.get("player").find(Filters.eq("UUID", pUUID.toString())).first();

            if (found == null) {
                BungeeSystem.plugin.getLogger().severe("[PlayerApi->saveAll] Spieler [" + pUUID.toString() + "] nicht gefunden!");
                return;
            }

            PlayerData pData = BungeeSystem.playerList.get(pUUID);

            BungeeSystem.mongo.collections.get("player").findOneAndUpdate(found, new Document("$set", pData.getDoc("player")));
            if (pData.ffaData != null)
                BungeeSystem.mongo.collections.get("ffa").findOneAndUpdate(found, new Document("$set", pData.getDoc("ffa")));
            if (pData.gtcData != null)
                BungeeSystem.mongo.collections.get("gtc").findOneAndUpdate(found, new Document("$set", pData.getDoc("gtc")));
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAllPermissions(ProxiedPlayer p) {
        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
        if (pData == null) {
            p.disconnect(new TextComponent("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden."));
            return null;
        }

        return pData.permissions;
    }
}
