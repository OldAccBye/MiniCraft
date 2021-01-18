package de.minicraft;

import org.bukkit.configuration.Configuration;

public class config {
    public static Configuration config;
    private static Configuration language;
    public static Configuration commandList;
    public static Configuration permissionsList;

    public static void setConfig(Configuration data) { config = data; }
    public static void setLanguage(Configuration data) { language = data; }
    public static void setCommandList(Configuration data) { commandList = data; }
    public static void setPermissionsList(Configuration data) { permissionsList = data; }

    public static String getLanguageText(String data) { return language.getString(config.getString("language") + "." + data); }
}