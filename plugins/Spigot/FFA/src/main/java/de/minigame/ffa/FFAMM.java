package de.minigame.ffa;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.UUID;

public class FFAMM {
    public MongoCollection<Document> players;

    public void connect() {
        String username = FFA.config.getString("mongodb.username"),
                password = FFA.config.getString("mongodb.password"),
                host = FFA.config.getString("mongodb.host"),
                database = FFA.config.getString("mongodb.database"),
                collection = FFA.config.getString("mongodb.collection");

        if (username == null || password == null || host == null || database == null || collection == null) {
            FFA.plugin.getLogger().severe("[FFAMM->connect] Please fill out all fields in the config.yml.");
            FFA.plugin.getServer().shutdown();
            return;
        }

        try {
            MongoClient mongoClient = MongoClients.create("mongodb+srv://" + username +  ":" + password + "@" + host + "/");
            MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
            this.players = mongoDatabase.getCollection(collection);
        } catch (MongoException e) {
            e.printStackTrace();
            FFA.plugin.getServer().shutdown();
        }
    }

    public boolean updatePlayerStats(UUID pUUID, Document d) {
        try {
            Document found = FFA.mongo.players.find(Filters.eq("UUID", pUUID.toString())).first();
            if (found == null) return false;

            FFA.mongo.players.findOneAndUpdate(found, new Document("$set", d));
        } catch (MongoException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}