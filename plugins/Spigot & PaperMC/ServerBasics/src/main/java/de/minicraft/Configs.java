package de.minicraft;

import de.minicraft.players.PlayerApi;
import de.minicraft.players.PlayerData;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Configs {
    public static Configuration config;
    public static Configuration language;
    public static Configuration commandList;
    public static Configuration permissionsList;

    public static void setConfig(Configuration data) { config = data; }
    public static void setLanguage(Configuration data) { language = data; }
    public static void setCommandList(Configuration data) { commandList = data; }
    public static void setPermissionsList(Configuration data) { permissionsList = data; }

    public static String getLanguageText(UUID pUUID, String data) {
        Player p = ServerBasics.plugin.getServer().getPlayer(pUUID);

        if (p == null) {
            ServerBasics.plugin.getLogger().severe("[Permissions] PlayerListener = null");
            return "null";
        }

        PlayerData pData = PlayerApi.get(pUUID);
        if (pData == null) {
            p.kickPlayer("Player data missing. Try to login again.");
            return "null";
        }

        return language.getString(pData.language + "." + data);
    }
}