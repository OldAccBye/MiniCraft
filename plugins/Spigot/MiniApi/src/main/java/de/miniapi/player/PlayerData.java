package de.miniapi.player;

import de.miniapi.Configs;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    public String username = "", group = "default", prefix = "";
    public Integer cookies = 0;
    public FFA ffaData = null;
    public GTC gtcData = null;
    public List<String> permissions = new ArrayList<>();

    public PlayerData(String username, String group, Integer cookies) {
        this.username = username;
        this.group = group;
        this.cookies = cookies;
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

        public GTC(int wins) {
            this.won = wins;
        }
    }

    public void updatePermissions() {
        this.permissions.clear();

        for (String permissionKey : Configs.permissionsList.getKeys(false)) {
            this.permissions.addAll(Configs.permissionsList.getStringList(permissionKey + ".permissions"));
            if (permissionKey.equals(this.group)) break;
        }
    }
}
