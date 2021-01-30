package de.minicraft;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.HashMap;
import java.util.List;

public class mongoManager {
    public HashMap<String, MongoCollection<Document>> collections = new HashMap<>();

    public void connect() {
        String username = config.config.getString("mongodb.username"),
                password = config.config.getString("mongodb.password"),
                host = config.config.getString("mongodb.host"),
                database = config.config.getString("mongodb.database");

        List<String> collectionsList = config.config.getStringList("mongodb.collections");

        if (username == null || password == null || host == null || database == null || collections == null) {
            serverBasics.plugin.getLogger().severe("[mongoManager->connect] Please fill out all fields in the config.yml.");
            serverBasics.plugin.getServer().shutdown();
            return;
        }

        try {
            MongoClient mongoClient = MongoClients.create("mongodb+srv://" + username +  ":" + password + "@" + host + "/");
            MongoDatabase mongoDatabase = mongoClient.getDatabase(database);
            for (String collectionName : collectionsList)
                this.collections.put(collectionName, mongoDatabase.getCollection(collectionName));
        } catch (MongoException e) {
            e.printStackTrace();
            serverBasics.plugin.getServer().shutdown();
        }
    }
}