package de.minicraft;

import de.minicraft.listener.block;
import de.minicraft.listener.chat;
import de.minicraft.listener.player;
import de.minicraft.players.commands.build;
import de.minicraft.players.commands.setgroup;
import de.minicraft.players.commands.setlanguage;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class serverBasics extends JavaPlugin {
    public static serverBasics plugin;
    public static mongoManager mongo = new mongoManager();

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

            config.setConfig(YamlConfiguration.loadConfiguration(file));
        }

        // language
        {
            file = new File(getDataFolder().getPath(), "language.yml");
            if (!file.exists()) {
                super.getLogger().severe(">>>>> [CONFIG] language.yml existiert nicht! <<<<<");
                return;
            }

            config.setLanguage(YamlConfiguration.loadConfiguration(file));
        }

        // commands
        {
            file = new File(getDataFolder().getPath(), "commands.yml");
            if (!file.exists()) {
                super.getLogger().severe(">>>>> [CONFIG] commands.yml existiert nicht! <<<<<");
                return;
            }

            config.setCommandList(YamlConfiguration.loadConfiguration(file));
        }

        // permissions
        {
            file = new File(getDataFolder().getPath(), "permissions.yml");
            if (!file.exists()) {
                super.getLogger().severe(">>>>> [CONFIG] permissions.yml existiert nicht! <<<<<");
                return;
            }

            config.setPermissionsList(YamlConfiguration.loadConfiguration(file));
        }

        /* ===== DATABASE - START ===== */
        mongo.connect();
        /* ===== DATABASE - END ===== */

        /* ===== COMMANDS - START ===== */
        Objects.requireNonNull(plugin.getCommand("setlanguage")).setExecutor(new setlanguage());
        Objects.requireNonNull(plugin.getCommand("setgroup")).setExecutor(new setgroup());
        Objects.requireNonNull(plugin.getCommand("build")).setExecutor(new build());
        /* ===== COMMANDS - END ===== */

        /* ===== LISTENER - START ===== */
        getServer().getPluginManager().registerEvents(new player(), this);
        getServer().getPluginManager().registerEvents(new block(), this);
        getServer().getPluginManager().registerEvents(new chat(), this);
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