package de.minicraft.players;

import java.util.Objects;
import java.util.UUID;

import com.mongodb.MongoException;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.permissions.PermissionAttachment;

import de.minicraft.serverBasics;

public class playerData {
    public UUID pUUID;
    public String username, language, group, banReason, bannedFrom;
    public Boolean banned;
    public Long banSinceTimestamp, banExpiresTimestamp, blockBreakEventTimestamp;
    public Integer blockBreakEventCounter;
    public PermissionAttachment permissions;

    playerData(UUID pUUID, String language) {
        this.pUUID = pUUID;
        this.language = language;
        this.permissions = Objects.requireNonNull(Bukkit.getPlayer(pUUID)).addAttachment(serverBasics.plugin);
    }

    public void saveAll() {
        try {
            Document found = serverBasics.mongo.collections.get("players").find(Filters.eq("UUID", this.pUUID.toString())).first();

            if (found == null) {
                Bukkit.getLogger().severe("[playerData->saveAll] Player " + this.username + " not found!");
                return;
            }

            Document update = new Document("$set", new Document("username", this.username)
                    .append("perm_group", this.group)
                    .append("language", this.language)
                    .append("banned", this.banned)
                    .append("banSinceTimestamp", this.banSinceTimestamp)
                    .append("banExpiresTimestamp", this.banExpiresTimestamp)
                    .append("banReason", this.banReason)
                    .append("bannedFrom", this.bannedFrom));

            serverBasics.mongo.collections.get("players").findOneAndUpdate(found, update);
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }
}