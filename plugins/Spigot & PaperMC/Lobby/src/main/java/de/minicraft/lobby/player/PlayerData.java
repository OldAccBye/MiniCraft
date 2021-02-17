package de.minicraft.lobby.player;

import java.util.Date;

public class PlayerData {
    public String group;
    public Integer cookies;
    public Long lastEmoteTime = new Date().getTime();

    public PlayerData(String group, int cookies) { this.group = group; this.cookies = cookies; }
}
