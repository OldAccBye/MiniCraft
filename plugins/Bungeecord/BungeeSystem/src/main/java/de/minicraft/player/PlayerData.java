package de.minicraft.player;

import org.bson.Document;

public class PlayerData {
    public String username, group, banReason, bannedFrom;
    public Boolean banned;
    public Long banSinceTimestamp, banExpiresTimestamp;

    public Document getDoc() {
        return new Document("$set", new Document("username", this.username)
                .append("perm_group", this.group)
                .append("banned", this.banned)
                .append("banSinceTimestamp", this.banSinceTimestamp)
                .append("banExpiresTimestamp", this.banExpiresTimestamp)
                .append("banReason", this.banReason)
                .append("bannedFrom", this.bannedFrom));
    }
}
