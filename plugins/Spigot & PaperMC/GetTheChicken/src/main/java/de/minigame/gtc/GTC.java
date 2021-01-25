package de.minigame.gtc;

import de.minigame.gtc.listener.player;
import de.minigame.gtc.players.commands.GTCommand;
import de.minigame.gtc.players.inventory;
import de.minigame.gtc.players.playerApi;
import de.minigame.gtc.players.playerData;
import de.minigame.gtc.players.scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;
import java.util.*;

public class GTC extends JavaPlugin {
    public static boolean roundStarted = false, preRoundStarted = false;
    private static int roundTaskId, preRoundTaskId, preRoundSeconds, roundSeconds;
    public static Location spawnLocation, roundLocation;
    public static String playWorld;
    public static GTC plugin;

    @Override
    public void onEnable() {
        plugin = this;

        // Config
        {
            File file = new File(super.getDataFolder(), "config.yml");
            if (file.exists()) {
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
                if (cfg.getString("World") != null) {
                    playWorld = cfg.getString("World");
                    Location Loc;

                    // spawn location
                    double roundX = cfg.getDouble("Round.x"), roundY = cfg.getDouble("Round.y"), roundZ = cfg.getDouble("Round.z");
                    float roundYaw = (float) cfg.getDouble("Round.yaw"), roundPitch = (float) cfg.getDouble("Round.pitch");
                    Loc = new Location(Bukkit.getWorld(playWorld), roundX, roundY, roundZ);
                    Loc.setYaw(roundYaw);
                    Loc.setPitch(roundPitch);
                    roundLocation = Loc;

                    // round location
                    double spawnX = cfg.getDouble("Spawn.x"), spawnY = cfg.getDouble("Spawn.y"), spawnZ = cfg.getDouble("Spawn.z");
                    float spawnYaw = (float) cfg.getDouble("Spawn.yaw"), spawnPitch = (float) cfg.getDouble("Spawn.pitch");
                    Loc = new Location(Bukkit.getWorld(playWorld), spawnX, spawnY, spawnZ);
                    Loc.setYaw(spawnYaw);
                    Loc.setPitch(spawnPitch);
                    spawnLocation = Loc;
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
        preRoundSeconds = 15;

        preRoundTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
           switch (preRoundSeconds) {
               case 15: case 10: case 5: case 4: case 3: case 2: case 1:
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
        roundSeconds = 20;

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

        roundTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            if (roundSeconds == 1)
                stopRound();
            else {
                roundSeconds--;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    playerData pData = playerApi.playerList.get(p.getUniqueId());
                    if (pData == null || !playerApi.playerList.get(p.getUniqueId()).inRound) continue;

                    float exp = p.getExp() - (float) 1/20;
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
            if (pData == null) {
                p.kickPlayer("Irgendetwas lief schief beim speichern :(");
                continue;
            }
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
    }
}
