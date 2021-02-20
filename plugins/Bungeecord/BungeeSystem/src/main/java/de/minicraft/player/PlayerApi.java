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
    public static boolean login(ProxiedPlayer p) {
        Document playerDoc = getData(p, "player");
        if (playerDoc == null) return false;
        PlayerData pData = new PlayerData();

        playerDoc.put("username", p.getName());
        for (String permissionKey : Configs.permissionsList.getKeys()) {
            pData.permissions.addAll(Configs.permissionsList.getStringList(permissionKey));
            if (permissionKey.equals(playerDoc.getString("group"))) break;
        }

        pData.data = playerDoc;
        BungeeSystem.playerList.put(p.getUniqueId(), pData);

        return true;
    }

    public static Document getData(ProxiedPlayer p, String collection) {
        Document playerDoc = new Document();

        try {
            switch (collection) {
                case "player" -> {
                    playerDoc = BungeeSystem.mongo.collections.get("player").find(Filters.eq("UUID", p.getUniqueId().toString())).first();

                    if (playerDoc == null) {
                        playerDoc = new Document("username", p.getName())
                                .append("UUID", p.getUniqueId().toString())
                                .append("cookies", 0)
                                .append("group", "default")
                                .append("friends", new ArrayList<>())
                                .append("securitycode", "null")
                                .append("banned", false)
                                .append("banSinceTimestamp", 0)
                                .append("banExpiresTimestamp", 0)
                                .append("banReason", "")
                                .append("bannedFrom", "");
                        BungeeSystem.mongo.collections.get("player").insertOne(playerDoc);
                        return playerDoc;
                    }
                }
                case "ffa" -> {
                    playerDoc = BungeeSystem.mongo.collections.get("ffa").find(Filters.eq("UUID", p.getUniqueId().toString())).first();

                    if (playerDoc == null) {
                        playerDoc = new Document("UUID", p.getUniqueId().toString())
                                .append("kills", 0)
                                .append("deaths", 0);
                        BungeeSystem.mongo.collections.get("ffa").insertOne(playerDoc);
                        return playerDoc;
                    }
                }
                case "gtc" -> {
                    playerDoc = BungeeSystem.mongo.collections.get("gtc").find(Filters.eq("UUID", p.getUniqueId().toString())).first();

                    if (playerDoc == null) {
                        playerDoc = new Document("UUID", p.getUniqueId().toString()).append("won", 0);
                        BungeeSystem.mongo.collections.get("gtc").insertOne(playerDoc);
                        return playerDoc;
                    }
                }
            }
        } catch (MongoException e) {
            e.printStackTrace();
            return null;
        }

        return playerDoc;
    }

    public static void saveAll(UUID pUUID) {
        try {
            PlayerData pData = BungeeSystem.playerList.get(pUUID);

            if (pData == null) {
                BungeeSystem.plugin.getLogger().severe("[PlayerApi->saveAll] Spieler [" + pUUID.toString() + "] nicht gefunden!");
                return;
            }

            BungeeSystem.mongo.collections.get("player").findOneAndUpdate(new Document("UUID", pUUID.toString()), new Document("$set", pData.data));

            if (pData.ffaData != null)
                BungeeSystem.mongo.collections.get("ffa").findOneAndUpdate(new Document("UUID", pUUID.toString()), new Document("$set", pData.ffaData));

            if (pData.gtcData != null)
                BungeeSystem.mongo.collections.get("gtc").findOneAndUpdate(new Document("UUID", pUUID.toString()), new Document("$set", pData.gtcData));
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
