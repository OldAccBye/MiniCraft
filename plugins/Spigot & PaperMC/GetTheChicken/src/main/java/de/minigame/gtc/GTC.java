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
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;
import java.util.*;

public class GTC extends JavaPlugin {
    public static boolean roundStarted = false, preRoundStarted = false;
    private static int roundTaskId, preRoundTaskId, preRoundSeconds, roundSeconds, playTime, preTime;
    public static Location spawnLocation, roundLocation;
    public static List<Location> chickenLocation = new ArrayList<>();
    public static String playWorld;
    public static GTC plugin;
    public static Entity lastChicken;

    @Override
    public void onEnable() {
        plugin = this;

        // Config
        {
            File file = new File(super.getDataFolder(), "config.yml");
            if (file.exists()) {
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                if (cfg.getString("World") != null) {
                    preTime = cfg.getInt("PreTime");
                    playTime = cfg.getInt("PlayTime");
                    playWorld = cfg.getString("World");
                    double x, y, z;
                    float yaw, pitch;
                    Location Loc;

                    // spawn location
                    x = cfg.getDouble("Round.x");
                    y = cfg.getDouble("Round.y");
                    z = cfg.getDouble("Round.z");
                    yaw = (float) cfg.getDouble("Round.yaw");
                    pitch = (float) cfg.getDouble("Round.pitch");
                    Loc = new Location(Bukkit.getWorld(playWorld), x, y, z);
                    Loc.setYaw(yaw);
                    Loc.setPitch(pitch);
                    roundLocation = Loc;

                    // round location
                    x = cfg.getDouble("Spawn.x");
                    y = cfg.getDouble("Spawn.y");
                    z = cfg.getDouble("Spawn.z");
                    yaw = (float) cfg.getDouble("Spawn.yaw");
                    pitch = (float) cfg.getDouble("Spawn.pitch");
                    Loc = new Location(Bukkit.getWorld(playWorld), x, y, z);
                    Loc.setYaw(yaw);
                    Loc.setPitch(pitch);
                    spawnLocation = Loc;

                    // chicken location1
                    x = cfg.getDouble("ChickenSpawn.pos1.x");
                    y = cfg.getDouble("ChickenSpawn.pos1.y");
                    z = cfg.getDouble("ChickenSpawn.pos1.z");
                    chickenLocation.add(new Location(Bukkit.getWorld(playWorld), x, y, z));

                    // chicken location2
                    x = cfg.getDouble("ChickenSpawn.pos2.x");
                    y = cfg.getDouble("ChickenSpawn.pos2.y");
                    z = cfg.getDouble("ChickenSpawn.pos2.z");
                    chickenLocation.add(new Location(Bukkit.getWorld(playWorld), x, y, z));
                } else
                    super.getLogger().severe("Bitte erstelle neue Spawns!");
            } else
                super.getLogger().severe("Datei 'config.yml' konnte nicht gefunden werden!");
        }

        plugin.getServer().getPluginManager().registerEvents(new player(), plugin);
        Objects.requireNonNull(plugin.getCommand("gtc")).setExecutor(new GTCommand());
    }

    @Override
    public void onDisable() {}

    public static void startPreRound() {
        preRoundStarted = true;
        preRoundSeconds = preTime;

        preRoundTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
           switch (preRoundSeconds) {
               case 5: case 4: case 3: case 2: case 1:
                   for (Player p : Bukkit.getOnlinePlayers())
                       p.sendMessage("§7Das Spiel startet in §a" + preRoundSeconds + " Sekunde(n)§7!");
                   break;
               case 0:
                   startRound();
                   break;
           }

           if (preRoundSeconds != 0)
               preRoundSeconds--;
        }, 0, 20);
    }

    public static void stopPreRound() {
        if (!preRoundStarted) return;

        Bukkit.getScheduler().cancelTask(preRoundTaskId);
        preRoundStarted = false;
    }

    public static void startRound() {
        Bukkit.getScheduler().cancelTask(preRoundTaskId);
        roundStarted = true;
        roundSeconds = playTime;

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            playerData pData = playerApi.playerList.get(p.getUniqueId());
            if (pData == null) continue;
            pData.inRound = true;
            p.teleport(roundLocation);
            inventory.reset(p);
            scoreboard.set(p);
            p.sendTitle("§3GTC", "Good luck!",  10, 70, 20);
            p.setExp(0.99f);
            p.setLevel(20);
        }

        spawnChicken();

        roundTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (roundSeconds == 1)
                stopRound();
            else {
                roundSeconds--;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    playerData pData = playerApi.playerList.get(p.getUniqueId());
                    if (pData == null || !playerApi.playerList.get(p.getUniqueId()).inRound) continue;

                    float exp = p.getExp() - (float) 1/playTime;
                    p.setExp(exp);
                    p.setLevel(roundSeconds);
                }
            }
        }, 0, 20);
    }

    public static void stopRound() {
        Bukkit.getScheduler().cancelTask(roundTaskId);
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
            p.teleport(spawnLocation);
            p.getInventory().clear();
            p.setExp(0.0f);
            p.setLevel(0);
        }

        if (!lastChicken.isDead())
            lastChicken.remove();
    }

    private static double getRandomDouble(double val1, double val2) {
        double min = Math.min(val1, val2);
        double max = Math.max(val1, val2);

        return (Math.random() * (max - min)) + min;
    }

    public static void spawnChicken() {
        double randomX = getRandomDouble(chickenLocation.get(0).getX(), chickenLocation.get(1).getX());
        double randomY = getRandomDouble(chickenLocation.get(0).getY(), chickenLocation.get(1).getY());
        double randomZ = getRandomDouble(chickenLocation.get(0).getZ(), chickenLocation.get(1).getZ());

        World w = Bukkit.getServer().getWorld(playWorld);
        if (w == null) return;
        lastChicken = w.spawnEntity(new Location(Bukkit.getWorld(playWorld), randomX, randomY, randomZ), EntityType.CHICKEN);
    }
}
