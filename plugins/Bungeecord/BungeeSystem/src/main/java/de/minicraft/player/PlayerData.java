package de.minicraft.player;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerData {
    public String username = "", group = "default", banReason = "", bannedFrom = "";
    public Boolean banned = false;
    public Long banSinceTimestamp = 0L, banExpiresTimestamp = 0L;
    public Integer cookies = 0;
    public List<String> friends = new ArrayList<>(), permissions = new ArrayList<>();
    public HashMap<String, Boolean> friendRequest = new HashMap<>();

    public Document getDoc() {
        return new Document("username", this.username)
                .append("group", this.group)
                .append("banned", this.banned)
                .append("banSinceTimestamp", this.banSinceTimestamp)
                .append("banExpiresTimestamp", this.banExpiresTimestamp)
                .append("banReason", this.banReason)
                .append("bannedFrom", this.bannedFrom)
                .append("friends", this.friends)
                .append("cookies", this.cookies);
    }
}