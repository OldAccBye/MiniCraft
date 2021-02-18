package de.minigame.ffa;

import de.minigame.ffa.listener.PlayerListener;
import de.minigame.ffa.players.FFAComands;
import de.minigame.ffa.players.FFAPlayerApi;
import de.minigame.ffa.players.FFAPlayerData;
import org.bson.Document;
import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class FFA extends JavaPlugin {
    public static FFAMM mongo = new FFAMM();
    public static Configuration config;
    public static Location spawnLoc;
    public static FFA plugin;

    @Override
    public void onEnable() {
        plugin = this;

        if (!getDataFolder().exists())
            if (!getDataFolder().mkdir()) plugin.getLogger().severe("[FILE] A new directory cannot be created!");

        File file;

        // Config
        {
            file = new File(getDataFolder().getPath(), "config.yml");
            if (!file.exists()) {
                plugin.getLogger().severe(">>>>> [CONFIG] config.yml existiert nicht! <<<<<");
                plugin.getServer().shutdown();
                return;
            }

            config = YamlConfiguration.loadConfiguration(file);
        }

        // Spawns
        {
            file = new File(getDataFolder().getPath(), "spawns.yml");
            if (!file.exists()) {
                super.getLogger().severe(">>>>> [CONFIG] spawns.yml existiert nicht! <<<<<");
                plugin.getServer().shutdown();
                return;
            }

            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            double x = cfg.getDouble("spawn.x"), y = cfg.getDouble("spawn.y"), z = cfg.getDouble("spawn.z");
            float yaw = (float) cfg.getDouble("spawn.yaw"), pitch = (float) cfg.getDouble("spawn.pitch");
            Location loc = new Location(plugin.getServer().getWorld("world"), x, y, z);
            loc.setYaw(yaw);
            loc.setPitch(pitch);
            spawnLoc = loc;
        }

        /* ===== LISTENER - START ===== */
        plugin.getServer().getPluginManager().registerEvents( new PlayerListener(), this);
        /* ===== LISTENER - START ===== */

        /* ===== COMMANDS - START ===== */
        Objects.requireNonNull(plugin.getCommand("setspawn")).setExecutor(new FFAComands());
        /* ===== COMMANDS - END ===== */

        /* ===== DATABASE - START ===== */
        mongo.connect();
        /* ===== DATABASE - END ===== */
    }

    @Override
    public void onDisable() {
        plugin.getLogger().info("Spieler werden gespeichert...");

        for (Map.Entry<UUID, FFAPlayerData> pData : FFAPlayerApi.playerList.entrySet()) {
            UUID key = pData.getKey();
            FFAPlayerData value = pData.getValue();

            if (!FFA.mongo.updatePlayerStats(key, new Document("kills", value.kills).append("deaths", value.deaths)))
                plugin.getLogger().severe("Spieler [" + key.toString() + "] konnte nicht gespeichert werden!");
        }

        plugin.getLogger().info("Spieler wurden gespeichert!");
    }
}
