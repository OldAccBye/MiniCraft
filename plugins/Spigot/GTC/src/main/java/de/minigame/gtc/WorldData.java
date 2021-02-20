package de.minigame.gtc;

import de.minigame.gtc.players.PlayerData;
import de.minigame.gtc.players.PlayerInventory;
import de.minigame.gtc.players.PlayerScoreboard;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WorldData {
    public static boolean roundStarted = false, preRoundStarted = false;
    public static int lastTaskId, preRoundSeconds, roundSeconds, playTime, preTime, playersToStart;
    public static Location spawnLocation, roundLocation;
    public static List<Location> chickenLocation = new ArrayList<>();
    public static Entity lastChicken;
    public static UUID topPlayerUUID;

    public static void startPreRound() {
        preRoundStarted = true;
        preRoundSeconds = preTime;

        for (Player players : GTC.plugin.getServer().getOnlinePlayers())
            players.sendMessage("§3GTC §7| §fDie Runde startet in §a" + preRoundSeconds + " Sekunden§f!");

        lastTaskId = GTC.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(GTC.plugin, () -> {
            if (preRoundSeconds > 0 && preRoundSeconds <= 5)
                for (Player players : GTC.plugin.getServer().getOnlinePlayers())
                    players.sendMessage("§3GTC §7| §fDie Runde startet in §a" + preRoundSeconds + " Sekunde(n)§f!");
            else if (preRoundSeconds == 0)
                startRound();

            if (preRoundSeconds != 0)
                preRoundSeconds--;
        }, 0, 20);
    }

    public static void stopPreRound() {
        GTC.plugin.getServer().getScheduler().cancelTask(lastTaskId);
        preRoundStarted = false;
    }

    public static void startRound() {
        GTC.plugin.getServer().getScheduler().cancelTask(lastTaskId);
        preRoundStarted = false;
        roundStarted = true;
        roundSeconds = playTime;

        GTC.plugin.getServer().getOnlinePlayers().forEach(players -> {
            PlayerData pData = GTC.playerList.get(players.getUniqueId());
            pData.inRound = true;
            pData.kills = 0;

            players.teleport(roundLocation);
            PlayerInventory.reset(players);
            PlayerScoreboard.set(players);
            players.sendTitle("§3GTC", "Viel Glück!",  10, 70, 20);
            players.setExp(0.99f);
            players.setLevel(20);
        });

        spawnChicken();

        lastTaskId = GTC.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(GTC.plugin, () -> {
            if (GTC.plugin.getServer().getOnlinePlayers().size() == 0) {
                roundStarted = false;
                roundSeconds = 0;
                if (lastChicken != null && !lastChicken.isDead())
                    lastChicken.remove();
                GTC.plugin.getServer().getScheduler().cancelTask(lastTaskId);
                return;
            }

            if (roundSeconds >= 1) {
                roundSeconds--;
                for (Player players : GTC.plugin.getServer().getOnlinePlayers()) {
                    float exp = players.getExp() - (float) 1/playTime;
                    players.setExp(Math.max(exp, 0.0f));
                    players.setLevel(roundSeconds);
                }
            }

            switch (roundSeconds) {
                case 5 -> GTC.plugin.getServer().getOnlinePlayers().forEach(players -> players.sendMessage("§3GTC §7| §fDie Runde endet in §a5 Sekunden§f!"));
                case 0 -> stopRound();
            }
        }, 0, 20);
    }

    public static void stopRound() {
        roundStarted = false;

        GTC.plugin.getServer().getScheduler().cancelTask(lastTaskId);

        Player topPlayer = GTC.plugin.getServer().getPlayer(topPlayerUUID);
        String topPlayerName = "null";
        if (topPlayer != null) {
            topPlayerName = topPlayer.getName();
            GTC.api.getPlayer(topPlayerUUID).gtcData.won += 1;
            if (!GTC.api.savePlayer(topPlayer))
                GTC.plugin.getLogger().severe("Spieler " + topPlayer.getUniqueId() + " konnte nicht gespeichert werden!");
        }

        for (Map.Entry<UUID, PlayerData> players : GTC.playerList.entrySet()) {
            PlayerData value = players.getValue();
            if (!value.inRound) continue;

            UUID key = players.getKey();

            Player p = GTC.plugin.getServer().getPlayer(key);
            if (p == null) {
                GTC.playerList.remove(key);
                continue;
            }

            value.inRound = false;
            PlayerScoreboard.set(p);

            p.sendTitle("§3Gewinner:", topPlayerName,  10, 70, 20);
            p.sendMessage("§3GTC §7| §eSpieler §6" + topPlayerName + " §egewann diese Runde!");
            p.teleport(spawnLocation);
            p.getInventory().clear();
            p.setExp(0.0f);
            p.setLevel(0);
        }

        if (lastChicken != null && !lastChicken.isDead())
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

        if (lastChicken != null && !lastChicken.isDead())
            lastChicken.remove();

        World world = GTC.plugin.getServer().getWorld("world");
        if (world != null)
            lastChicken = world.spawnEntity(new Location(world, randomX, randomY, randomZ), EntityType.CHICKEN);
    }
}