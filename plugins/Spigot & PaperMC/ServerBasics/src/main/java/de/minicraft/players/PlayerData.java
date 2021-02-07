package de.minicraft.players;

import org.bson.Document;
import org.bukkit.permissions.PermissionAttachment;

public class PlayerData {
    public String username, group, banReason, bannedFrom;
    public Boolean banned;
    public Long banSinceTimestamp, banExpiresTimestamp, blockBreakEventTimestamp;
    public Integer blockBreakEventCounter;
    public PermissionAttachment permissions;

    PlayerData(PermissionAttachment permAttachment) { this.permissions = permAttachment; }

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