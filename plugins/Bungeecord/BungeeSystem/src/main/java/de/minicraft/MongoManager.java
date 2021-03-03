package de.minicraft;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoManager {
    public MongoCollection<Document> player, banned, online;

    public void connect() {
        String username = Configs.config.getString("mongodb.username"),
                password = Configs.config.getString("mongodb.password"),
                host = Configs.config.getString("mongodb.host"),
                database = Configs.config.getString("mongodb.database");

        if (username == null || password == null || host == null || database == null) {
            BungeeSystem.plugin.getLogger().severe("[MongoManager->connect] Bitte füll alle Daten in der config.yml aus.");
            BungeeSystem.plugin.getProxy().stop();
            return;
        }

        try {
            MongoClient mongoClient = MongoClients.create("mongodb+srv://" + username +  ":" + password + "@" + host + "/");
            MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
            this.player = mongoDatabase.getCollection("player");
            this.banned = mongoDatabase.getCollection("banned");
            this.online = mongoDatabase.getCollection("online");
        } catch (MongoException e) {
            e.printStackTrace();
            BungeeSystem.plugin.getProxy().stop();
        }
    }
}
