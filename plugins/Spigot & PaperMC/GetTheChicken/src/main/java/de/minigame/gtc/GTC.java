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
import org.bukkit.WorldCreator;
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
        File file;

        // Config
        {
            file = new File(super.getDataFolder(), "config.yml");
            if (file.exists()) {
                YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

                for (String worldName : cfg.getKeys(false)) {
                    if (Bukkit.getServer().getWorld(worldName) == null) {
                        file = new File(Bukkit.getServer().getWorldContainer(), worldName);
                        if (!file.exists()) {
                            super.getLogger().severe("Die Welt mit dem Namen [" + worldName + "] konnte nicht als Ordner gefunden werden!");
                            continue;
                        }

                        new WorldCreator(worldName).environment(World.Environment.NORMAL).generateStructures(false).createWorld();
                    }

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
                    Loc = new Location(Bukkit.getServer().getWorld(worldName), x, y, z);
                    Loc.setYaw(yaw);
                    Loc.setPitch(pitch);
                    w.roundLocation = Loc;

                    // round location
                    x = cfg.getDouble(worldName + ".Spawn.x");
                    y = cfg.getDouble(worldName + ".Spawn.y");
                    z = cfg.getDouble(worldName + ".Spawn.z");
                    yaw = (float) cfg.getDouble(worldName + ".Spawn.yaw");
                    pitch = (float) cfg.getDouble(worldName + ".Spawn.pitch");
                    Loc = new Location(Bukkit.getServer().getWorld(worldName), x, y, z);
                    Loc.setYaw(yaw);
                    Loc.setPitch(pitch);
                    w.spawnLocation = Loc;

                    // chicken location1
                    x = cfg.getDouble(worldName + ".ChickenSpawn.pos1.x");
                    y = cfg.getDouble(worldName + ".ChickenSpawn.pos1.y");
                    z = cfg.getDouble(worldName + ".ChickenSpawn.pos1.z");
                    w.chickenLocation.add(new Location(Bukkit.getServer().getWorld(worldName), x, y, z));

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
        World w = Bukkit.getServer().getWorld(worldName);
        if (w == null) return;
        worldData wData = worldLists.get(worldName);
        wData.preRoundStarted = true;
        wData.preRoundSeconds = wData.preTime;

        for (Player p : w.getPlayers())
            p.sendMessage("§7Das Spiel startet in §a" + wData.preRoundSeconds + " Sekunden§7!");

        wData.preRoundTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
           switch (wData.preRoundSeconds) {
               case 5: case 4: case 3: case 2: case 1:
                   for (Player p : w.getPlayers())
                       p.sendMessage("§7Das Spiel startet in §a" + wData.preRoundSeconds + " Sekunde(n)§7!");
                   break;
               case 0:
                   startRound(worldName);
                   break;
           }

           if (wData.preRoundSeconds != 0)
               wData.preRoundSeconds--;
        }, 0, 20);
    }

    public static void stopPreRound(String worldName) {
        worldData w = worldLists.get(worldName);
        if (!w.preRoundStarted) return;

        Bukkit.getServer().getScheduler().cancelTask(w.preRoundTaskId);
        w.preRoundStarted = false;
    }

    public static void startRound(String worldName) {
        worldData wData = worldLists.get(worldName);
        Bukkit.getServer().getScheduler().cancelTask(wData.preRoundTaskId);
        wData.preRoundStarted = false;
        wData.roundStarted = true;
        wData.roundSeconds = wData.playTime;

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            playerData pData = playerApi.playerList.get(p.getUniqueId());
            if (pData == null) continue;
            pData.inRound = true;
            p.teleport(wData.roundLocation);
            inventory.reset(p);
            scoreboard.set(p);
            p.sendTitle("§3GTC", "Viel Glück!",  10, 70, 20);
            p.setExp(0.99f);
            p.setLevel(20);
        }

        spawnChicken(worldName);

        wData.roundTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            World w = Bukkit.getServer().getWorld(worldName);
            if (w == null || w.getPlayers().size() == 0) {
                wData.roundStarted = false;
                wData.roundSeconds = 0;
                if (!wData.lastChicken.isDead())
                    wData.lastChicken.remove();
                Bukkit.getServer().getScheduler().cancelTask(wData.roundTaskId);
                return;
            }

            if (wData.roundSeconds == 1)
                stopRound(worldName);
            else {
                wData.roundSeconds--;
                for (Player p : w.getPlayers()) {
                    playerData pData = playerApi.playerList.get(p.getUniqueId());
                    if (pData == null || !playerApi.playerList.get(p.getUniqueId()).inRound) continue;

                    float exp = p.getExp() - (float) 1/wData.playTime;
                    p.setExp(exp);
                    p.setLevel(wData.roundSeconds);
                }
            }
        }, 0, 20);
    }

    public static void stopRound(String worldName) {
        World w = Bukkit.getServer().getWorld(worldName);
        if (w == null) return;
        worldData wData = worldLists.get(worldName);
        wData.roundStarted = false;

        Bukkit.getServer().getScheduler().cancelTask(wData.roundTaskId);
        HashMap<UUID, Integer> players = new HashMap<>();

        for (playerData data : playerApi.playerList.values())
            if (data.inRound)
                players.put(data.pUUID, data.kills);

        UUID topPlayerUUID = Collections.max(players.entrySet(), Map.Entry.comparingByValue()).getKey();

        String topPlayerName = "null";
        Player topPlayer = Bukkit.getServer().getPlayer(topPlayerUUID);
        if (topPlayer != null) topPlayerName = topPlayer.getName();

        for (Player p : w.getPlayers()) {
            p.sendTitle("§3Gewonnen hat:", topPlayerName,  10, 70, 20);
            p.sendMessage("§eDer Spieler §6" + topPlayerName + " §egewann mit §6" + players.get(topPlayerUUID) + " §ekills!");

            playerData pData = playerApi.playerList.get(p.getUniqueId());
            if (pData != null) {
                pData.inRound = false;
                pData.kills = 0;
            }

            ScoreboardManager manager = Bukkit.getServer().getScoreboardManager();
            if (manager != null)
                p.setScoreboard(manager.getNewScoreboard());

            p.teleport(wData.spawnLocation);
            p.getInventory().clear();
            p.setExp(0.0f);
            p.setLevel(0);
        }

        if (wData.lastChicken != null && !wData.lastChicken.isDead())
            wData.lastChicken.remove();
    }

    private static double getRandomDouble(double val1, double val2) {
        double min = Math.min(val1, val2);
        double max = Math.max(val1, val2);

        return (Math.random() * (max - min)) + min;
    }

    public static void spawnChicken(String worldName) {
        worldData wData = worldLists.get(worldName);
        double randomX = getRandomDouble(wData.chickenLocation.get(0).getX(), wData.chickenLocation.get(1).getX());
        double randomY = getRandomDouble(wData.chickenLocation.get(0).getY(), wData.chickenLocation.get(1).getY());
        double randomZ = getRandomDouble(wData.chickenLocation.get(0).getZ(), wData.chickenLocation.get(1).getZ());

        World w = Bukkit.getServer().getWorld(worldName);
        if (w != null)
            wData.lastChicken = w.spawnEntity(new Location(Bukkit.getServer().getWorld(worldName), randomX, randomY, randomZ), EntityType.CHICKEN);
    }
}