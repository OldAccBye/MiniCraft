package de.minicraft;

import de.minicraft.player.commands.*;
import de.minicraft.listener.ChatListener;
import de.minicraft.listener.PlayerListener;
import de.minicraft.listener.TablistListener;
import de.minicraft.player.PlayerApi;
import de.minicraft.player.PlayerData;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class BungeeSystem extends Plugin {
    public static BungeeSystem plugin = null;
    public static MongoManager mongo = new MongoManager();
    public static final HashMap<UUID, PlayerData> playerList = new HashMap<>();

    @Override
    public void onEnable() {
        plugin = this;
        File file;

        if (!getDataFolder().exists())
            if (!getDataFolder().mkdir()) plugin.getLogger().severe("[FILE] A new directory cannot be created!");

        // Config
        {
            file = new File(getDataFolder().getPath(), "config.yml");
            if (!file.exists()) {
                super.getLogger().severe(">>>>> [CONFIG] config.yml existiert nicht! <<<<<");
                return;
            }

            try {
                Configs.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // Commands
        {
            file = new File(getDataFolder().getPath(), "commands.yml");
            if (!file.exists()) {
                super.getLogger().severe(">>>>> [CONFIG] commands.yml existiert nicht! <<<<<");
                return;
            }

            try {
                Configs.commandList = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // Permissions
        {
            file = new File(getDataFolder().getPath(), "permissions.yml");
            if (!file.exists()) {
                super.getLogger().severe(">>>>> [CONFIG] permissions.yml existiert nicht! <<<<<");
                return;
            }

            try {
                Configs.permissionsList = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        // Listener
        plugin.getProxy().getPluginManager().registerListener(plugin, new PluginMessageReceiver());
        plugin.getProxy().getPluginManager().registerListener(plugin, new TablistListener());
        plugin.getProxy().getPluginManager().registerListener(plugin, new PlayerListener());
        plugin.getProxy().getPluginManager().registerListener(plugin, new ChatListener());

        // Commands
        plugin.getProxy().getPluginManager().registerCommand(plugin, new HubCommand());
        plugin.getProxy().getPluginManager().registerCommand(plugin, new FriendsCommand());
        plugin.getProxy().getPluginManager().registerCommand(plugin, new SetGroupCommand());
        plugin.getProxy().getPluginManager().registerCommand(plugin, new TpServerCommand());
        plugin.getProxy().getPluginManager().registerCommand(plugin, new KickCommand());
        plugin.getProxy().getPluginManager().registerCommand(plugin, new BanCommand());

        // Channels
        plugin.getProxy().registerChannel("bungeesystem:lobby");
        plugin.getProxy().registerChannel("bungeesystem:minibasics");

        // Mongo
        mongo.connect();
    }

    @Override
    public void onDisable() {
        plugin.getProxy().getLogger().warning("[playerList] Daten werden gespeichert...");

        for (Map.Entry<UUID, PlayerData> pData : playerList.entrySet())
            PlayerApi.saveAll(pData.getKey());

        plugin.getProxy().getLogger().warning("[playerList] Daten wurden gespeichert!");
    }
}
