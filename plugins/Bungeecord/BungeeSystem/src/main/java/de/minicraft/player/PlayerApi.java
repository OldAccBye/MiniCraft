package de.minicraft.player;

import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import de.minicraft.BungeeSystem;
import de.minicraft.Configs;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Projections.*;

public class PlayerApi {
    public static boolean login(ProxiedPlayer p) {
        Document playerDoc = getData(p);
        if (playerDoc == null) return false;
        PlayerData pData = new PlayerData();

        for (String permissionKey : Configs.permissionsList.getKeys()) {
            pData.permissions.addAll(Configs.permissionsList.getStringList(permissionKey));
            if (permissionKey.equals(playerDoc.getString("group"))) break;
        }

        pData.data = playerDoc;
        BungeeSystem.playerList.put(p.getUniqueId(), pData);
        return BungeeSystem.playerList.containsKey(p.getUniqueId());
    }

    public static Document getData(ProxiedPlayer p) {
        Document playerDoc;

        try {
            playerDoc = BungeeSystem.mongo.player.find(Filters.eq("UUID", p.getUniqueId().toString())).projection(fields(exclude("_id"),include("group"), include("friends"))).first();

            if (playerDoc == null) {
                playerDoc = new Document("UUID", p.getUniqueId().toString())
                        .append("username", p.getName())
                        .append("cookies", 0)
                        .append("group", "default")
                        .append("premiumTimestamp", 0L)
                        .append("friends", new ArrayList<>())
                        .append("securitycode", "null")
                        .append("registrationTimestamp", new Date().getTime());
                BungeeSystem.mongo.player.insertOne(playerDoc);
                return new Document("group", "default").append("friends", new ArrayList<>());
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

            BungeeSystem.mongo.player.findOneAndUpdate(new Document("UUID", pUUID.toString()), new Document("$set", pData.data));
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getAllPermissions(ProxiedPlayer p) {
        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
        if (pData == null) {
            p.disconnect(new TextComponent("[b-gap-01] Es konnten keine Spielerdaten gefunden werden. Melde dies im Support, sollte dieser Fehler erneut auftauchen."));
            return null;
        }

        return pData.permissions;
    }
}
