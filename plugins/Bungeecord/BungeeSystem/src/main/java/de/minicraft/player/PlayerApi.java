package de.minicraft.player;

import com.mongodb.MongoException;
import com.mongodb.MongoWriteException;
import com.mongodb.client.model.Filters;
import de.minicraft.BungeeSystem;
import de.minicraft.Configs;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class PlayerApi {
    private static Document register(UUID pUUID, ProxiedPlayer p) {
        try {
            BungeeSystem.mongo.collections.get("players").insertOne(new Document("username", p.getName())
                    .append("UUID", pUUID.toString())
                    .append("perm_group", "default")
                    .append("language", "en")
                    .append("banned", false)
                    .append("banSinceTimestamp", 0)
                    .append("banExpiresTimestamp", 0)
                    .append("banReason", "")
                    .append("bannedFrom", ""));
        } catch (MongoWriteException e) {
            e.printStackTrace();
            return new Document("status", "error").append("reason", "[PlayerApi->register] Daten konnte nicht gespeichert werden.");
        } catch (MongoException e) {
            e.printStackTrace();
            return new Document("status", "error").append("reason", "[PlayerApi->register] Irgendetwas ist schief gelaufen.");
        }

        PlayerData data = new PlayerData();
        data.username = p.getName();
        BungeeSystem.playerList.put(pUUID, data);

        return new Document("status", "success");
    }

    public static Document login(UUID pUUID) {
        ProxiedPlayer p = BungeeSystem.plugin.getProxy().getPlayer(pUUID);
        if (p == null) return new Document("status", "error").append("reason", "[PlayerApi->login] p = null");

        // Daten aus der Datenbank werden hier zwischen gespeichert
        Document playerDoc;

        try {
            playerDoc = BungeeSystem.mongo.collections.get("players").find(Filters.eq("UUID", pUUID.toString())).first();

            if (playerDoc == null)
                return register(pUUID, p);
        } catch (MongoException e) {
            e.printStackTrace();
            return new Document("status", "error").append("reason", "[PlayerApi->login] Irgendetwas ist schief gelaufen.");
        }

        PlayerData data = new PlayerData();
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
        BungeeSystem.playerList.put(pUUID, data);

        return new Document("status", "success");
    }

    public static void saveAll(UUID pUUID) {
        try {
            Document found = BungeeSystem.mongo.collections.get("players").find(Filters.eq("UUID", pUUID.toString())).first();

            if (found == null) {
                BungeeSystem.plugin.getLogger().severe("[PlayerApi->saveAll] Spieler [" + pUUID.toString() + "] nicht gefunden!");
                return;
            }

            BungeeSystem.mongo.collections.get("players").findOneAndUpdate(found, new Document("$set", BungeeSystem.playerList.get(pUUID).getDoc()));
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
