package de.minigame.ffa.players;

import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import de.minigame.ffa.FFA;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class playerApi {
    public static final HashMap<UUID, playerData> playerList = new HashMap<>();

    public static boolean register(Player p) {
        try {
            FFA.mongo.players.insertOne(new Document("UUID", p.getUniqueId())
                    .append("kills", 0)
                    .append("deaths", 0));
        } catch (MongoException e) {
            e.printStackTrace();
            p.kickPlayer("[playerApi->register] Something went wrong.");
            return false;
        }

        playerData data = new playerData();
        data.pUUID = p.getUniqueId();
        data.kills = 0;
        data.deaths = 0;
        playerList.put(p.getUniqueId(), data);
        return true;
    }

    public static boolean login(UUID pUUID) {
        Player p = Bukkit.getPlayer(pUUID);
        if (p == null) return false;

        Document playerDoc;

        try {
            playerDoc = FFA.mongo.players.find(Filters.eq("UUID", pUUID.toString())).first();

            if (playerDoc == null) return register(p);
        } catch (MongoException e) {
            e.printStackTrace();
            p.kickPlayer("[playerApi->login] Something went wrong.");
            return false;
        }

        playerData data = new playerData();
        data.pUUID = pUUID;
        data.kills = playerDoc.getInteger("kills");
        data.deaths = playerDoc.getInteger("deaths");
        playerList.put(pUUID, data);
        return true;
    }
}
