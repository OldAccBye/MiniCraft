package de.minicraft;

import de.minicraft.listener.BlockListener;
import de.minicraft.listener.ChatListener;
import de.minicraft.listener.PlayerListener;
import de.minicraft.listener.PluginMessageReceiver;
import de.minicraft.players.PlayerData;
import de.minicraft.players.commands.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public final class ServerBasics extends JavaPlugin {
    public static ServerBasics plugin;
    public static final HashMap<UUID, PlayerData> playerList = new HashMap<>();

    @Override
    public void onEnable() {
        plugin = this;
        File file;

        if (!getDataFolder().exists())
            if (!getDataFolder().mkdir()) plugin.getLogger().severe("[FILE] A new directory cannot be created!");

        // prefix
        {
            file = new File(getDataFolder().getPath(), "prefix.yml");
            if (!file.exists())
                super.getLogger().severe(">>>>> [CONFIG] prefix.yml existiert nicht! <<<<<");

            Configs.prefix = YamlConfiguration.loadConfiguration(file);
        }

        // commands
        {
            file = new File(getDataFolder().getPath(), "commands.yml");
            if (!file.exists())
                super.getLogger().severe(">>>>> [CONFIG] commands.yml existiert nicht! <<<<<");

            Configs.commandList = YamlConfiguration.loadConfiguration(file);
        }

        // permissions
        {
            file = new File(getDataFolder().getPath(), "permissions.yml");
            if (!file.exists())
                super.getLogger().severe(">>>>> [CONFIG] permissions.yml existiert nicht! <<<<<");

            Configs.permissionsList = YamlConfiguration.loadConfiguration(file);
        }

        /* ===== COMMANDS - START ===== */
        Objects.requireNonNull(plugin.getCommand("setgroup")).setExecutor(new SetgroupCommand());
        Objects.requireNonNull(plugin.getCommand("build")).setExecutor(new BuildCommand());
        Objects.requireNonNull(plugin.getCommand("bc")).setExecutor(new BroadcastCommand());
        Objects.requireNonNull(plugin.getCommand("tp")).setExecutor(new TpCommand());
        Objects.requireNonNull(plugin.getCommand("tpallhere")).setExecutor(new TpAllHereCommand());
        /* ===== COMMANDS - END ===== */

        /* ===== LISTENER - START ===== */
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new BlockListener(), this);
        plugin.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        /* ===== LISTENER - END ===== */

        /* ===== CHANNELS - START ===== */
        // Outgoing
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(this, "basics:command");

        // Incoming
        plugin.getServer().getMessenger().registerIncomingPluginChannel( this, "bungeesystem:player", new PluginMessageReceiver());
        /* ===== CHANNELS - END ===== */

        System.out.println(" ");
        System.out.println("###################################");
        System.out.println("# ServerBasics wurde gestartet!");
        System.out.println("# Plugin by EntenKoeniq.");
        System.out.println("###################################");
        System.out.println(" ");
    }

    @Override
    public void onDisable() {
        System.out.println(" ");
        System.out.println("###################################");
        System.out.println("# ServerBasics wurde deaktiviert!");
        System.out.println("# Plugin by EntenKoeniq.");
        System.out.println("###################################");
        System.out.println(" ");
    }
}