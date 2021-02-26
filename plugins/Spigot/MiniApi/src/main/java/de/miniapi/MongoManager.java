package de.miniapi;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoManager {
    public MongoCollection<Document> playerData, gameData;

    public void connect() {
        String username = Configs.config.getString("mongodb.username"),
                password = Configs.config.getString("mongodb.password"),
                host = Configs.config.getString("mongodb.host"),
                database = Configs.config.getString("mongodb.database");

        if (username == null || password == null || host == null || database == null) {
            MiniApi.plugin.getLogger().severe("[MongoManager->connect] Bitte füll alle Daten in der config.yml aus.");
            MiniApi.plugin.getServer().shutdown();
            return;
        }

        try {
            MongoClient mongoClient = MongoClients.create("mongodb+srv://" + username +  ":" + password + "@" + host + "/");
            MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
            this.playerData = mongoDatabase.getCollection("player");

            switch (Configs.serverName) {
                case "ffa" -> this.gameData = mongoDatabase.getCollection("ffa");
                case "gtc" -> this.gameData = mongoDatabase.getCollection("gtc");
            }
        } catch (MongoException e) {
            e.printStackTrace();
            MiniApi.plugin.getServer().shutdown();
        }
    }
}
