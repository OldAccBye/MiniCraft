package de.minigame.gtc;

import de.minigame.gtc.listener.PlayerListener;
import de.minigame.gtc.players.PlayerData;
import de.minigame.gtc.players.commands.GTCommand;

import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class GTC extends JavaPlugin {
    public static final HashMap<UUID, PlayerData> playerList = new HashMap<>();
    public static GTC plugin;

    @Override
    public void onEnable() {
        plugin = this;
        File file = new File(super.getDataFolder(), "config.yml");
        if (!file.exists()) super.getLogger().severe("Datei 'config.yml' konnte nicht gefunden werden!");

        // World
        {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

            WorldData.maxPlayers = cfg.getInt("MaxPlayers");
            WorldData.playersToStart = cfg.getInt("PlayersToStart");
            WorldData.preTime = cfg.getInt("PreTime");
            WorldData.playTime = cfg.getInt("PlayTime");
            double x, y, z;
            float yaw, pitch;
            Location Loc;

            // spawn location
            x = cfg.getDouble("Round.x");
            y = cfg.getDouble("Round.y");
            z = cfg.getDouble("Round.z");
            yaw = (float) cfg.getDouble("Round.yaw");
            pitch = (float) cfg.getDouble("Round.pitch");
            Loc = new Location(plugin.getServer().getWorld("world"), x, y, z);
            Loc.setYaw(yaw);
            Loc.setPitch(pitch);
            WorldData.roundLocation = Loc;

            // round location
            x = cfg.getDouble("Spawn.x");
            y = cfg.getDouble("Spawn.y");
            z = cfg.getDouble("Spawn.z");
            yaw = (float) cfg.getDouble("Spawn.yaw");
            pitch = (float) cfg.getDouble("Spawn.pitch");
            Loc = new Location(plugin.getServer().getWorld("world"), x, y, z);
            Loc.setYaw(yaw);
            Loc.setPitch(pitch);
            WorldData.spawnLocation = Loc;

            // chicken location1
            x = cfg.getDouble("ChickenSpawn.pos1.x");
            y = cfg.getDouble("ChickenSpawn.pos1.y");
            z = cfg.getDouble("ChickenSpawn.pos1.z");
            WorldData.chickenLocation.add(new Location(plugin.getServer().getWorld("world"), x, y, z));

            // chicken location2
            x = cfg.getDouble("ChickenSpawn.pos2.x");
            y = cfg.getDouble("ChickenSpawn.pos2.y");
            z = cfg.getDouble("ChickenSpawn.pos2.z");
            WorldData.chickenLocation.add(new Location(plugin.getServer().getWorld("world"), x, y, z));
        }

        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
        Objects.requireNonNull(plugin.getCommand("gtc")).setExecutor(new GTCommand());
    }

    @Override
    public void onDisable() {}
}