package de.minicraft;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;

public class MongoManager {
    public HashMap<String, MongoCollection<Document>> collections = new HashMap<>();

    public void connect() {
        String username = Configs.config.getString("mongodb.username"),
                password = Configs.config.getString("mongodb.password"),
                host = Configs.config.getString("mongodb.host"),
                database = Configs.config.getString("mongodb.database");

        List<String> collectionsList = Configs.config.getStringList("mongodb.collections");

        if (username == null || password == null || host == null || database == null || collections == null) {
            ServerBasics.plugin.getLogger().severe("[MongoManager->connect] Please fill out all fields in the Configs.yml.");
            ServerBasics.plugin.getServer().shutdown();
            return;
        }

        try {
            MongoClient mongoClient = MongoClients.create("mongodb+srv://" + username +  ":" + password + "@" + host + "/");
            MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
            for (String collectionName : collectionsList)
                this.collections.put(collectionName, mongoDatabase.getCollection(collectionName));
        } catch (MongoException e) {
            e.printStackTrace();
            ServerBasics.plugin.getServer().shutdown();
        }
    }
}