package de.minigame.ffa;

import de.minigame.ffa.listener.player;
import de.minigame.ffa.players.commands;
import de.minigame.ffa.players.playerApi;
import de.minigame.ffa.players.playerData;
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
    public static mongoManager mongo = new mongoManager();
    public static Configuration config;
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
                super.getLogger().severe(">>>>> [CONFIG] config.yml existiert nicht! <<<<<");
                plugin.getServer().shutdown();
                return;
            }

            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            double x = cfg.getDouble("spawn.x"), y = cfg.getDouble("spawn.y"), z = cfg.getDouble("spawn.z");
            float yaw = (float) cfg.getDouble("spawn.yaw"), pitch = (float) cfg.getDouble("spawn.pitch");
            Location loc = new Location(plugin.getServer().getWorld("world"), x, y, z);
            loc.setYaw(yaw);
            loc.setPitch(pitch);
            spawnData.loc = loc;
        }

        /* ===== LISTENER - START ===== */
        plugin.getServer().getPluginManager().registerEvents( new player(), this);
        /* ===== LISTENER - START ===== */

        /* ===== COMMANDS - START ===== */
        Objects.requireNonNull(plugin.getCommand("setspawn")).setExecutor(new commands());
        /* ===== COMMANDS - END ===== */

        /* ===== DATABASE - START ===== */
        mongo.connect();
        /* ===== DATABASE - END ===== */
    }

    @Override
    public void onDisable() {
        plugin.getLogger().info("Spieler werden gespeichert...");

        for (Map.Entry<UUID, playerData> pData : playerApi.playerList.entrySet()) {
            UUID key = pData.getKey();
            playerData value = pData.getValue();

            if (!FFA.mongo.updatePlayerStats(key, new Document("kills", value.kills).append("deaths", value.deaths)))
                plugin.getLogger().severe("Player [" + key.toString() + "] could not be saved!");
        }

        plugin.getLogger().info("Spieler wurden gespeichert!");
    }
}
