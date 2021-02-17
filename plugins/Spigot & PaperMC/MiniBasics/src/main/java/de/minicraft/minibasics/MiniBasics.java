package de.minicraft.minibasics;

import de.minicraft.minibasics.listener.ChatListener;
import de.minicraft.minibasics.listener.PlayerListener;
import de.minicraft.minibasics.player.PlayerData;
import de.minicraft.minibasics.player.commands.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public final class MiniBasics extends JavaPlugin {
    public static MiniBasics plugin;
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

        // commands
        {
            file = new File(getDataFolder().getPath(), "commands.yml");
            if (!file.exists())
                super.getLogger().severe(">>>>> [CONFIG] commands.yml existiert nicht! <<<<<");

            Configs.commandsList = YamlConfiguration.loadConfiguration(file);
        }

        // permissions
        {
            file = new File(getDataFolder().getPath(), "permissions.yml");
            if (!file.exists())
                super.getLogger().severe(">>>>> [CONFIG] permissions.yml existiert nicht! <<<<<");

            Configs.permissionsList = YamlConfiguration.loadConfiguration(file);
        }

        /* ===== COMMANDS - START ===== */
        Objects.requireNonNull(plugin.getCommand("build")).setExecutor(new BuildCommand());
        Objects.requireNonNull(plugin.getCommand("tp")).setExecutor(new TpCommand());
        /* ===== COMMANDS - END ===== */

        /* ===== LISTENER - START ===== */
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        /* ===== LISTENER - END ===== */

        /* ===== CHANNELS - START ===== */
        // Incoming
        plugin.getServer().getMessenger().registerIncomingPluginChannel(this, "bungeesystem:minibasics", new PluginMessageReceiver());
        /* ===== CHANNELS - END ===== */
    }

    @Override
    public void onDisable() { }
}