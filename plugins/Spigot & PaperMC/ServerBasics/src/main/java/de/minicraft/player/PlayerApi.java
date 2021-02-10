package de.minicraft.player;

import java.util.UUID;

import de.minicraft.ServerBasics;

import org.bson.Document;
import org.bukkit.entity.Player;

public class PlayerApi {
    public static Document preLogin(Player p, String group) {
        PlayerData data = new PlayerData(p.addAttachment(ServerBasics.plugin));
        data.username = p.getName();
        data.group = group;
        ServerBasics.playerList.put(p.getUniqueId(), data);
        return new Document("boolean", true);
    }

    public static PlayerData get(UUID pUUID) { return ServerBasics.playerList.get(pUUID); }

    public static void logout(UUID pUUID) { ServerBasics.playerList.remove(pUUID); }
}