package de.minigame.ffa.players;

import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import de.minigame.ffa.FFA;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class FFAPlayerApi {
    public static final HashMap<UUID, FFAPlayerData> playerList = new HashMap<>();

    public static boolean register(Player p) {
        try {
            FFA.mongo.players.insertOne(new Document("UUID", p.getUniqueId().toString())
                    .append("kills", 0)
                    .append("deaths", 0));
        } catch (MongoException e) {
            e.printStackTrace();
            p.kickPlayer("[02] Something went wrong.");
            return false;
        }

        FFAPlayerData data = new FFAPlayerData();
        data.kills = 0;
        data.deaths = 0;
        playerList.put(p.getUniqueId(), data);
        return true;
    }

    public static boolean login(UUID pUUID) {
        Player p = FFA.plugin.getServer().getPlayer(pUUID);
        if (p == null) return false;

        Document playerDoc;

        try {
            playerDoc = FFA.mongo.players.find(Filters.eq("UUID", pUUID.toString())).first();
            if (playerDoc == null) return register(p);
        } catch (MongoException e) {
            e.printStackTrace();
            p.kickPlayer("[01] Something went wrong.");
            return false;
        }

        FFAPlayerData data = new FFAPlayerData();
        data.kills = playerDoc.getInteger("kills");
        data.deaths = playerDoc.getInteger("deaths");
        playerList.put(pUUID, data);
        return true;
    }

    public static void logout(UUID pUUID) {
        if (!playerList.containsKey(pUUID)) return;

        if (!FFA.mongo.updatePlayerStats(pUUID, new Document("kills", playerList.get(pUUID).kills).append("deaths", playerList.get(pUUID).deaths)))
            FFA.plugin.getLogger().severe("Spieler [" + pUUID + "] konnte nicht gespeichert werden!");

        playerList.remove(pUUID);
    }
}
