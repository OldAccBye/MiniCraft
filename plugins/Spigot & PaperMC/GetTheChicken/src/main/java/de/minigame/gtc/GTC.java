package de.minigame.gtc;

import de.minigame.gtc.listener.player;
import de.minigame.gtc.players.commands.GTCommand;
import de.minigame.gtc.players.inventory;
import de.minigame.gtc.players.playerApi;
import de.minigame.gtc.players.playerData;
import de.minigame.gtc.players.scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;
import java.util.*;

public class GTC extends JavaPlugin {
    public final static HashMap<String, worldData> worldLists = new HashMap<>();
    public static GTC plugin;


    @Override
    public void onEnable() {
        plugin = this;

        // Config
        {
            File file = new File(super.getDataFolder(), "config.yml");
            if (file.exists()) {
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

                for (String worldName : cfg.getKeys(false)) {
                    worldData w = new worldData();
                    w.worldName = worldName;
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
                    Loc = new Location(Bukkit.getWorld(worldName), x, y, z);
                    Loc.setYaw(yaw);
                    Loc.setPitch(pitch);
                    w.roundLocation = Loc;

                    // round location
                    x = cfg.getDouble(worldName + ".Spawn.x");
                    y = cfg.getDouble(worldName + ".Spawn.y");
                    z = cfg.getDouble(worldName + ".Spawn.z");
                    yaw = (float) cfg.getDouble(worldName + ".Spawn.yaw");
                    pitch = (float) cfg.getDouble(worldName + ".Spawn.pitch");
                    Loc = new Location(Bukkit.getWorld(worldName), x, y, z);
                    Loc.setYaw(yaw);
                    Loc.setPitch(pitch);
                    w.spawnLocation = Loc;

                    // chicken location1
                    x = cfg.getDouble(worldName + ".ChickenSpawn.pos1.x");
                    y = cfg.getDouble(worldName + ".ChickenSpawn.pos1.y");
                    z = cfg.getDouble(worldName + ".ChickenSpawn.pos1.z");
                    w.chickenLocation.add(new Location(Bukkit.getWorld(worldName), x, y, z));

                    // chicken location2
                    x = cfg.getDouble(worldName + ".ChickenSpawn.pos2.x");
                    y = cfg.getDouble(worldName + ".ChickenSpawn.pos2.y");
                    z = cfg.getDouble(worldName + ".ChickenSpawn.pos2.z");
                    w.chickenLocation.add(new Location(Bukkit.getWorld(worldName), x, y, z));

                    worldLists.put(worldName, w);
                }
            } else
                super.getLogger().severe("Datei 'config.yml' konnte nicht gefunden werden!");
        }

        plugin.getServer().getPluginManager().registerEvents(new player(), plugin);
        Objects.requireNonNull(plugin.getCommand("gtc")).setExecutor(new GTCommand());
    }

    @Override
    public void onDisable() {}

    public static void startPreRound(String worldName) {
        worldData w = worldLists.get(worldName);
        w.preRoundStarted = true;
        w.preRoundSeconds = w.preTime;

        w.preRoundTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
           switch (w.preRoundSeconds) {
               case 5: case 4: case 3: case 2: case 1:
                   for (Player p : Bukkit.getOnlinePlayers())
                       p.sendMessage("§7Das Spiel startet in §a" + w.preRoundSeconds + " Sekunde(n)§7!");
                   break;
               case 0:
                   startRound(worldName);
                   break;
           }

           if (w.preRoundSeconds != 0)
               w.preRoundSeconds--;
        }, 0, 20);
    }

    public static void stopPreRound(String worldName) {
        worldData w = worldLists.get(worldName);
        if (!w.preRoundStarted) return;

        Bukkit.getScheduler().cancelTask(w.preRoundTaskId);
        w.preRoundStarted = false;
    }

    public static void startRound(String worldName) {
        worldData w = worldLists.get(worldName);
        Bukkit.getScheduler().cancelTask(w.preRoundTaskId);
        w.roundStarted = true;
        w.roundSeconds = w.playTime;

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            playerData pData = playerApi.playerList.get(p.getUniqueId());
            if (pData == null) continue;
            pData.inRound = true;
            p.teleport(w.roundLocation);
            inventory.reset(p);
            scoreboard.set(p);
            p.sendTitle("§3GTC", "Good luck!",  10, 70, 20);
            p.setExp(0.99f);
            p.setLevel(20);
        }

        spawnChicken(worldName);

        w.roundTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (w.roundSeconds == 1)
                stopRound(worldName);
            else {
                w.roundSeconds--;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    playerData pData = playerApi.playerList.get(p.getUniqueId());
                    if (pData == null || !playerApi.playerList.get(p.getUniqueId()).inRound) continue;

                    float exp = p.getExp() - (float) 1/w.playTime;
                    p.setExp(exp);
                    p.setLevel(w.roundSeconds);
                }
            }
        }, 0, 20);
    }

    public static void stopRound(String worldName) {
        worldData w = worldLists.get(worldName);
        Bukkit.getScheduler().cancelTask(w.roundTaskId);
        HashMap<UUID, Integer> players = new HashMap<>();

        for (playerData data : playerApi.playerList.values())
            if (data.inRound)
                players.put(data.pUUID, data.kills);

        UUID topPlayerUUID = Collections.max(players.entrySet(), Map.Entry.comparingByValue()).getKey();

        Player topPlayer = Bukkit.getPlayer(topPlayerUUID);
        if (topPlayer == null) return;

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.sendTitle("§3Gewonnen hat:", topPlayer.getName(),  10, 70, 20);
            playerData pData = playerApi.playerList.get(p.getUniqueId());
            if (pData == null) continue;
            pData.inRound = false;
            pData.kills = 0;
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            if (manager == null) continue;
            p.setScoreboard(manager.getNewScoreboard());
            p.teleport(w.spawnLocation);
            p.getInventory().clear();
            p.setExp(0.0f);
            p.setLevel(0);
        }

        if (!w.lastChicken.isDead())
            w.lastChicken.remove();
    }

    private static double getRandomDouble(double val1, double val2) {
        double min = Math.min(val1, val2);
        double max = Math.max(val1, val2);

        return (Math.random() * (max - min)) + min;
    }

    public static void spawnChicken(String worldName) {
        worldData w = worldLists.get(worldName);
        double randomX = getRandomDouble(w.chickenLocation.get(0).getX(), w.chickenLocation.get(1).getX());
        double randomY = getRandomDouble(w.chickenLocation.get(0).getY(), w.chickenLocation.get(1).getY());
        double randomZ = getRandomDouble(w.chickenLocation.get(0).getZ(), w.chickenLocation.get(1).getZ());

        World w2 = Bukkit.getServer().getWorld(worldName);
        if (w2 == null) return;
        w.lastChicken = w2.spawnEntity(new Location(Bukkit.getWorld(worldName), randomX, randomY, randomZ), EntityType.CHICKEN);
    }
}
