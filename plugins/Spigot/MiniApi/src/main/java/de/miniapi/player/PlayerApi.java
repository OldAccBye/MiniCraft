package de.miniapi.player;

import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import de.miniapi.Configs;
import de.miniapi.MiniApi;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.UUID;

import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.fields;

public class PlayerApi {
    public static boolean login(Player p) {
        Document playerDoc = getData(p, "playerData");
        if (playerDoc == null) return false;
        playerDoc.replace("username", p.getName());

        PlayerData pData = new PlayerData();
        pData.data = playerDoc;
        pData.prefix = Configs.permissionsList.getString(pData.data.getString("group") + ".prefix");
        pData.gameData = getData(p, "gameData");
        pData.updatePermissions();
        MiniApi.playerList.put(p.getUniqueId(), pData);

        p.updateCommands();
        return true;
    }

    public static Document getData(Player p, String collection) {
        Document playerDoc = new Document();

        try {
            switch (collection) {
                case "playerData" -> playerDoc = MiniApi.mongo.playerData.find(Filters.eq("UUID", p.getUniqueId().toString())).projection(fields(exclude("_id"), exclude("UUID"), exclude("friends"))).first();
                case "gameData" -> {
                    if (MiniApi.mongo.gameData == null) return null;
                    playerDoc = MiniApi.mongo.gameData.find(Filters.eq("UUID", p.getUniqueId().toString())).first();

                    if (playerDoc == null) {
                        switch (Configs.serverName) {
                            case "ffa" -> playerDoc = new Document("UUID", p.getUniqueId().toString()).append("kills", 0).append("deaths", 0);
                            case "gtc" -> playerDoc = new Document("UUID", p.getUniqueId().toString()).append("won", 0);
                            default -> {
                                return null;
                            }
                        }

                        MiniApi.mongo.gameData.insertOne(playerDoc);
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
            PlayerData pData = MiniApi.playerList.get(pUUID);

            if (pData == null) {
                MiniApi.plugin.getLogger().severe("[PlayerApi->saveAll] Spieler [" + pUUID.toString() + "] nicht gefunden!");
                return;
            }

            MiniApi.mongo.playerData.findOneAndUpdate(new Document("UUID", pUUID.toString()), new Document("$set", pData.data));

            if (pData.gameData != null)
                MiniApi.mongo.gameData.findOneAndUpdate(new Document("UUID", pUUID.toString()), new Document("$set", pData.gameData));
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }
}
