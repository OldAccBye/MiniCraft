package de.minigame.ffa;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class FFA extends JavaPlugin {
    public FFA plugin;
    public static mongoManager mongo = new mongoManager();
    public static Configuration config;

    @Override
    public void onEnable() {
        plugin = this;

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

        /* ===== DATABASE - START ===== */
        mongo.connect();
        /* ===== DATABASE - END ===== */
    }

    @Override
    public void onDisable() {}

    public void loadSpawn() {
        File file = new File(getDataFolder().getPath(), "spawns.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        double x = cfg.getDouble("spawn.x"), y = cfg.getDouble("spawn.y"), z = cfg.getDouble("spawn.z");
        float yaw = (float) cfg.getDouble("spawn.yaw"), pitch = (float) cfg.getDouble("spawn.pitch");
        String wn = cfg.getString("spawn.worldname");
        Location loc = new Location(Bukkit.getWorld(wn), x, y, z);
        loc.setYaw(yaw);
        loc.setPitch(pitch);
        data.loc = loc;
    }

    public boolean createSpawn() {
        File file = new File(getDataFolder().getPath(), "spawns.yml");
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        return true;
    }
}
