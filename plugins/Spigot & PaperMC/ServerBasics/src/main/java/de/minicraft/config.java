package de.minicraft;

import de.minicraft.players.playerApi;
import org.bukkit.configuration.Configuration;

import java.util.UUID;

public class config {
    public static Configuration config;
    public static Configuration language;
    public static Configuration commandList;
    public static Configuration permissionsList;

    public static void setConfig(Configuration data) { config = data; }
    public static void setLanguage(Configuration data) { language = data; }
    public static void setCommandList(Configuration data) { commandList = data; }
    public static void setPermissionsList(Configuration data) { permissionsList = data; }

    public static String getLanguageText(UUID pUUID, String data) { return language.getString(playerApi.get(pUUID).language + "." + data); }
}