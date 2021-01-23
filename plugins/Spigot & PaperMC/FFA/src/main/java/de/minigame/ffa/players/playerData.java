package de.minigame.ffa.players;

import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import de.minigame.ffa.FFA;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.UUID;

public class playerData {
    public UUID pUUID;
    public int kills, deaths;

    public void saveAll() {
        try {
            Document found = FFA.mongo.players.find(Filters.eq("UUID", this.pUUID.toString())).first();

            if (found == null) {
                Bukkit.getLogger().severe("[playerData->saveAll] Player not found!");
                return;
            }

            Document update = new Document("$set", new Document("kills", this.kills).append("deaths", this.deaths));

            FFA.mongo.players.findOneAndUpdate(found, update);
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }
}
