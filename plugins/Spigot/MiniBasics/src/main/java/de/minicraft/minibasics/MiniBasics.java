package de.minicraft.minibasics;

import de.miniapi.MiniApi;
import de.minicraft.minibasics.listener.ChatListener;
import de.minicraft.minibasics.player.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class MiniBasics extends JavaPlugin {
    public static MiniBasics plugin;
    public static MiniApi api = (MiniApi) Bukkit.getPluginManager().getPlugin("MiniApi");

    @Override
    public void onEnable() {
        plugin = this;

        if (!getDataFolder().exists() && !getDataFolder().mkdir()) {
            plugin.getLogger().severe("Es konnte kein Ordner für dieses Plugin erstellt werden!");
            plugin.getServer().shutdown();
            return;
        }

        // Load commands
        {
            File file = new File(getDataFolder().getPath(), "commands.yml");
            if (!file.exists())
                super.getLogger().severe(">>>>> [CONFIG] commands.yml existiert nicht! <<<<<");

            Configs.commandsList = YamlConfiguration.loadConfiguration(file);
        }

        /* ===== COMMANDS - START ===== */
        Objects.requireNonNull(plugin.getCommand("build")).setExecutor(new BuildCommand());
        Objects.requireNonNull(plugin.getCommand("tp")).setExecutor(new TpCommand());
        /* ===== COMMANDS - END ===== */

        /* ===== LISTENER - START ===== */
        plugin.getServer().getPluginManager().registerEvents(new ChatListener(), this);
        /* ===== LISTENER - END ===== */
    }

    @Override
    public void onDisable() { }
}