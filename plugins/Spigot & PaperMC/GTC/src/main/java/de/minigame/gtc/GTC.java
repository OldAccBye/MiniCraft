package de.minigame.gtc;

import de.minigame.gtc.listener.PlayerListener;
import de.minigame.gtc.players.commands.GTCommand;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class GTC extends JavaPlugin {
    public final static HashMap<String, WorldData> worldLists = new HashMap<>();
    public static final HashMap<UUID, Integer> playerList = new HashMap<>();
    public static GTC plugin;

    @Override
    public void onEnable() {
        plugin = this;
        File file;

        // Config
        {
            file = new File(super.getDataFolder(), "config.yml");
            if (file.exists()) {
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

                for (String worldName : cfg.getKeys(false)) {
                    if (plugin.getServer().getWorld(worldName) == null) {
                        file = new File(plugin.getServer().getWorldContainer(), worldName);
                        if (!file.exists()) {
                            super.getLogger().severe("Die Welt mit dem Namen [" + worldName + "] konnte nicht als Ordner gefunden werden!");
                            continue;
                        }

                        new WorldCreator(worldName).environment(World.Environment.NORMAL).generateStructures(false).createWorld();
                    }

                    WorldData w = new WorldData(worldName);
                    w.maxPlayers = cfg.getInt(worldName + ".MaxPlayers");
                    w.playersToStart = cfg.getInt(worldName + ".PlayersToStart");
                    w.preTime = cfg.getInt(worldName + ".PreTime");
                    w.playTime = cfg.getInt(worldName + ".PlayTime");
                    double x, y, z;
                    float yaw, pitch;
                    Location Loc;

                    // spawn location
                    x = cfg.getDouble(worldName + ".Round.x");
                    y = cfg.getDouble(worldName + ".Round.y");
                    z = cfg.getDouble(worldName + ".Round.z");
                    yaw = (float) cfg.getDouble(worldName + ".Round.yaw");
                    pitch = (float) cfg.getDouble(worldName + ".Round.pitch");
                    Loc = new Location(plugin.getServer().getWorld(worldName), x, y, z);
                    Loc.setYaw(yaw);
                    Loc.setPitch(pitch);
                    w.roundLocation = Loc;

                    // round location
                    x = cfg.getDouble(worldName + ".Spawn.x");
                    y = cfg.getDouble(worldName + ".Spawn.y");
                    z = cfg.getDouble(worldName + ".Spawn.z");
                    yaw = (float) cfg.getDouble(worldName + ".Spawn.yaw");
                    pitch = (float) cfg.getDouble(worldName + ".Spawn.pitch");
                    Loc = new Location(plugin.getServer().getWorld(worldName), x, y, z);
                    Loc.setYaw(yaw);
                    Loc.setPitch(pitch);
                    w.spawnLocation = Loc;

                    // chicken location1
                    x = cfg.getDouble(worldName + ".ChickenSpawn.pos1.x");
                    y = cfg.getDouble(worldName + ".ChickenSpawn.pos1.y");
                    z = cfg.getDouble(worldName + ".ChickenSpawn.pos1.z");
                    w.chickenLocation.add(new Location(plugin.getServer().getWorld(worldName), x, y, z));

                    // chicken location2
                    x = cfg.getDouble(worldName + ".ChickenSpawn.pos2.x");
                    y = cfg.getDouble(worldName + ".ChickenSpawn.pos2.y");
                    z = cfg.getDouble(worldName + ".ChickenSpawn.pos2.z");
                    w.chickenLocation.add(new Location(plugin.getServer().getWorld(worldName), x, y, z));

                    worldLists.put(worldName, w);
                }
            } else
                super.getLogger().severe("Datei 'config.yml' konnte nicht gefunden werden!");
        }

        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
        Objects.requireNonNull(plugin.getCommand("gtc")).setExecutor(new GTCommand());
    }

    @Override
    public void onDisable() {}
}