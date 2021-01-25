package de.minigame.ffa.players;

import de.minigame.ffa.FFA;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.UUID;

public class playerData {
    public UUID pUUID;
    public int kills, killstreak = 0, deaths;

    public void saveAll() {
        if (!FFA.mongo.updatePlayer(this.pUUID, new Document("kills", this.kills).append("deaths", this.deaths)))
            Bukkit.getLogger().severe("Player [" + this.pUUID + "] could not be saved!");
    }
}
