package de.minicraft;

import de.minicraft.player.commands.HubCommand;
import de.minicraft.listener.ChatListener;
import de.minicraft.listener.PlayerListener;
import de.minicraft.listener.PluginMessageReceiver;
import de.minicraft.listener.Tablist;
import de.minicraft.player.PlayerApi;
import de.minicraft.player.PlayerData;
import de.minicraft.player.commands.SetGroupCommand;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class BungeeSystem extends Plugin {
    public static BungeeSystem plugin = null;
    public static MongoManager mongo = new MongoManager();
    public static final HashMap<UUID, PlayerData> playerList = new HashMap<>();
    private static ScheduledTask checkPlayerTask;

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
        plugin.getProxy().getPluginManager().registerListener(plugin, new Tablist());
        plugin.getProxy().getPluginManager().registerListener(plugin, new PlayerListener());
        plugin.getProxy().getPluginManager().registerListener(plugin, new ChatListener());

        // Commands
        plugin.getProxy().getPluginManager().registerCommand(plugin, new HubCommand());
        plugin.getProxy().getPluginManager().registerCommand(plugin, new SetGroupCommand());

        // Channels
        plugin.getProxy().registerChannel("lobby:server");
        plugin.getProxy().registerChannel("basics:command");
        plugin.getProxy().registerChannel("bungeesystem:player");

        // Mongo
        mongo.connect();

        // Check player task
        checkPlayer();
    }

    @Override
    public void onDisable() {
        checkPlayerTask.cancel();

        plugin.getProxy().getLogger().warning("[playerList] Daten werden gespeichert...");

        for (Map.Entry<UUID, PlayerData> pData : playerList.entrySet())
            PlayerApi.saveAll(pData.getKey());

        plugin.getProxy().getLogger().warning("[playerList] Daten wurden gespeichert!");
    }

    private static void checkPlayer() {
        checkPlayerTask = plugin.getProxy().getScheduler().schedule(plugin, () -> {
            plugin.getProxy().getLogger().warning("[playerList] Prüfung gestartet...");

            for (Map.Entry<UUID, PlayerData> pData : playerList.entrySet()) {
                ProxiedPlayer p = plugin.getProxy().getPlayer(pData.getKey());

                if (p == null) {
                    plugin.getProxy().getLogger().info("[playerList] Spieler " + pData.getValue().username + " wird abgemeldet.");
                    PlayerApi.saveAll(pData.getKey());
                    PlayerApi.logout(pData.getKey());
                }
            }

            plugin.getProxy().getLogger().warning("[playerList] Prüfung beendet!");
        }, 5, 5, TimeUnit.MINUTES);
    }
}
