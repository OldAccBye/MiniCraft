package de.miniapi;

import de.miniapi.listener.PlayerListener;
import de.miniapi.player.PlayerData;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class MiniApi extends JavaPlugin {
    public static MiniApi plugin;
    public static final HashMap<UUID, PlayerData> playerList = new HashMap<>();

    @Override
    public void onEnable() {
        plugin = this;
        File file;

        if (!getDataFolder().exists() && !getDataFolder().mkdir()) {
            plugin.getLogger().severe("Es konnte kein Ordner für dieses Plugin erstellt werden!");
            plugin.getServer().shutdown();
            return;
        }

        // Load permissions
        {
            file = new File(getDataFolder().getPath(), "permissions.yml");
            if (!file.exists()) {
                plugin.getLogger().severe("Es konnte keine permissions.yml geladen werden!");
                plugin.getServer().shutdown();
            }

            Configs.permissionsList = YamlConfiguration.loadConfiguration(file);
        }

        // Load config
        {
            file = new File(getDataFolder().getPath(), "config.yml");
            if (!file.exists()) {
                plugin.getLogger().severe("Es konnte keine config.yml geladen werden!");
                plugin.getServer().shutdown();
            }

            Configuration cfg = YamlConfiguration.loadConfiguration(file);
            Configs.serverName = cfg.getString("Server");
        }

        /* ===== LISTENER - START ===== */
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
        /* ===== LISTENER - END ===== */

        /* ===== CHANNELS - START ===== */
        // Incoming
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "bungeesystem:miniapi", new PluginMessageReceiver());

        // Outgoing
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "bungeesystem:miniapi");
        /* ===== CHANNELS - END ===== */
    }

    public PlayerData getPlayer(UUID pUUID) {
        return playerList.get(pUUID);
    }
}
