package de.minigame.ffa;

import de.minigame.ffa.listener.player;
import de.minigame.ffa.players.commands;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class FFA extends JavaPlugin {
    public static mongoManager mongo = new mongoManager();
    public static Configuration config;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists())
            if (!getDataFolder().mkdir()) Bukkit.getLogger().severe("[FILE] A new directory cannot be created!");

        // Config
        {
            File file = new File(getDataFolder().getPath(), "config.yml");
            if (!file.exists()) {
                super.getLogger().severe(">>>>> [CONFIG] config.yml existiert nicht! <<<<<");
                Bukkit.shutdown();
                return;
            }

            config = YamlConfiguration.loadConfiguration(file);
        }

        loadSpawn();

        /* ===== LISTENER - START ===== */
        getServer().getPluginManager().registerEvents( new player(), this);
        /* ===== LISTENER - START ===== */

        /* ===== COMMANDS - START ===== */
        Objects.requireNonNull(getCommand("setspawn")).setExecutor(new commands());
        /* ===== COMMANDS - END ===== */

        /* ===== DATABASE - START ===== */
        mongo.connect();
        /* ===== DATABASE - END ===== */
    }

    @Override
    public void onDisable() {}

    public void loadSpawn() {
        File file = new File(getDataFolder().getPath(), "spawns.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        if (cfg.getString("spawn.x") == null) {
            super.getLogger().severe("Keine Daten in der spawns.yml gefunden!");
            Bukkit.shutdown();
            return;
        }

        double x = cfg.getDouble("spawn.x"), y = cfg.getDouble("spawn.y"), z = cfg.getDouble("spawn.z");
        float yaw = (float) cfg.getDouble("spawn.yaw"), pitch = (float) cfg.getDouble("spawn.pitch");
        Location loc = new Location(Bukkit.getWorld("world"), x, y, z);
        loc.setYaw(yaw);
        loc.setPitch(pitch);
        spawnData.loc = loc;
    }
}
