package de.minicraft;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;

public class mongoManager {
    public MongoCollection<Document> players;

    public void connect() {
        try {
            MongoClient mongoClient = MongoClients.create("mongodb+srv://miniuser:minipass@minicraft.kxkh9.mongodb.net/");
            MongoDatabase mongoDatabase = mongoClient.getDatabase("MiniCraft");
            this.players = mongoDatabase.getCollection("players");
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getServer().shutdown();
        }
    }
}