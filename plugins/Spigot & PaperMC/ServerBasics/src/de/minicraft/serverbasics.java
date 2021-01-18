package de.minicraft;

import de.minicraft.listener.chat;
import de.minicraft.listener.player;
import de.minicraft.players.commands.setgroup;
import de.minicraft.players.commands.setlanguage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;
import java.util.Objects;

public final class serverbasics extends JavaPlugin {
    public static serverbasics plugin;

    @Override
    public void onEnable() {
        plugin = this;
        File file;

        if (!getDataFolder().exists())
            if (!getDataFolder().mkdir()) Bukkit.getLogger().severe("[FILE] A new directory cannot be created!");

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
        super.getLogger().info(">>>>> [DB] Verbindung wird hergestellt... <<<<<");

        try {
            if (database.connect())
                super.getLogger().info(">>>>> [DB] Verbindung wurde hergestellt! <<<<<");
            else
                super.getLogger().severe(">>>>> [DB] Verbindung konnte nicht hergestellt werden! <<<<<");
        } catch (ClassNotFoundException | SQLException e) {
            super.getLogger().severe(e.getMessage());
        }
        /* ===== DATABASE - END ===== */

        /* ===== COMMANDS - START ===== */
        Objects.requireNonNull(plugin.getCommand("setlanguage")).setExecutor(new setlanguage());
        Objects.requireNonNull(plugin.getCommand("setgroup")).setExecutor(new setgroup());
        /* ===== COMMANDS - END ===== */

        /* ===== LISTENER - START ===== */
        getServer().getPluginManager().registerEvents(new player(), this);
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
        try {
            if (database.disconnect())
                super.getLogger().info(">>>>> [DB] Verbindung geschlossen! <<<<<");
            else
                super.getLogger().severe(">>>>> [DB] Verbindung konnte nicht geschlossen werden! <<<<<");
        } catch (SQLException e) {
            super.getLogger().severe(e.getMessage());
        }

        System.out.println(" ");
        System.out.println("###################################");
        System.out.println("# ServerBasics wurde deaktiviert!");
        System.out.println("# Plugin by EntenKoeniq.");
        System.out.println("###################################");
        System.out.println(" ");
    }
}