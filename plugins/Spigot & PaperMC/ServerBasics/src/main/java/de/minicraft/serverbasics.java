package de.minicraft;

import de.minicraft.listener.BlockListener;
import de.minicraft.listener.ChatListener;
import de.minicraft.listener.PlayerListener;
import de.minicraft.players.commands.BuildCommand;
import de.minicraft.players.commands.SetgroupCommand;
import de.minicraft.players.commands.SetlanguageCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class ServerBasics extends JavaPlugin {
    public static ServerBasics plugin;
    public static SBMM mongo = new SBMM();

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

            SBConfig.setConfig(YamlConfiguration.loadConfiguration(file));
        }

        // language
        {
            file = new File(getDataFolder().getPath(), "language.yml");
            if (!file.exists()) {
                super.getLogger().severe(">>>>> [CONFIG] language.yml existiert nicht! <<<<<");
                return;
            }

            SBConfig.setLanguage(YamlConfiguration.loadConfiguration(file));
        }

        // commands
        {
            file = new File(getDataFolder().getPath(), "commands.yml");
            if (!file.exists()) {
                super.getLogger().severe(">>>>> [CONFIG] commands.yml existiert nicht! <<<<<");
                return;
            }

            SBConfig.setCommandList(YamlConfiguration.loadConfiguration(file));
        }

        // permissions
        {
            file = new File(getDataFolder().getPath(), "permissions.yml");
            if (!file.exists()) {
                super.getLogger().severe(">>>>> [CONFIG] permissions.yml existiert nicht! <<<<<");
                return;
            }

            SBConfig.setPermissionsList(YamlConfiguration.loadConfiguration(file));
        }

        /* ===== DATABASE - START ===== */
        mongo.connect();
        /* ===== DATABASE - END ===== */

        /* ===== COMMANDS - START ===== */
        Objects.requireNonNull(plugin.getCommand("setlanguage")).setExecutor(new SetlanguageCommand());
        Objects.requireNonNull(plugin.getCommand("setgroup")).setExecutor(new SetgroupCommand());
        Objects.requireNonNull(plugin.getCommand("build")).setExecutor(new BuildCommand());
        /* ===== COMMANDS - END ===== */

        /* ===== LISTENER - START ===== */
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        /* ===== LISTENER - END ===== */

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