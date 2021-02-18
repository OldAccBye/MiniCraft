package de.minicraft.player;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerData {
    public String username = "", group = "default", banReason = "", bannedFrom = "", securitycode = "null";
    public Boolean banned = false;
    public Long banSinceTimestamp = 0L, banExpiresTimestamp = 0L;
    public Integer cookies = 0;
    public List<String> friends = new ArrayList<>(), permissions = new ArrayList<>();
    public HashMap<String, Boolean> friendRequest = new HashMap<>();
    public FFA ffaData = null;
    public GTC gtcData = null;

    public Document getDoc(String collection) {
        switch (collection) {
            case "player" -> {
                return new Document("username", this.username)
                        .append("cookies", this.cookies)
                        .append("group", this.group)
                        .append("friends", this.friends)
                        .append("securitycode", this.securitycode)
                        .append("banned", this.banned)
                        .append("banSinceTimestamp", this.banSinceTimestamp)
                        .append("banExpiresTimestamp", this.banExpiresTimestamp)
                        .append("banReason", this.banReason)
                        .append("bannedFrom", this.bannedFrom);
            }
            case "ffa" -> {
                return new Document("kills", this.ffaData.kills).append("deaths", this.ffaData.deaths);
            }
            case "gtc" -> {
                return new Document("won", this.gtcData.won);
            }
        }

        return new Document();
    }

    public static class FFA {
        public Integer kills = 0, deaths = 0;

        public FFA(int kills, int deaths) {
            this.kills = kills;
            this.deaths = deaths;
        }
    }

    public static class GTC {
        public Integer won = 0;

        public GTC(int won) {
            this.won = won;
        }
    }
}