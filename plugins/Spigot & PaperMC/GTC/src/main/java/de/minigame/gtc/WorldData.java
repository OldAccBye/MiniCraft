package de.minigame.gtc;

import de.minigame.gtc.players.PlayerInventory;
import de.minigame.gtc.players.PlayerScoreboard;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WorldData {
    public final World world;
    public boolean roundStarted = false, preRoundStarted = false;
    public int lastTaskId, preRoundSeconds, roundSeconds, playTime, preTime, players, maxPlayers, playersToStart;
    public Location spawnLocation, roundLocation;
    public List<Location> chickenLocation = new ArrayList<>();
    public Entity lastChicken;
    public UUID topPlayer;

    WorldData(String worldName) { this.world = GTC.plugin.getServer().getWorld(worldName); }

    public void startPreRound() {
        this.preRoundStarted = true;
        this.preRoundSeconds = this.preTime;

        for (Player p : world.getPlayers())
            p.sendMessage("§3GTC §7| §fThe game starts in §a" + this.preRoundSeconds + " seconds§f!");

        this.lastTaskId = GTC.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(GTC.plugin, () -> {
            if (this.preRoundSeconds > 0 && this.preRoundSeconds <= 5)
                world.getPlayers().forEach(players -> players.sendMessage("§3GTC §7| §fThe game starts in §a" + this.preRoundSeconds + " second(n)§f!"));
            else if (this.preRoundSeconds == 0)
                startRound();

            if (this.preRoundSeconds != 0)
                this.preRoundSeconds--;
        }, 0, 20);
    }

    public void stopPreRound() {
        GTC.plugin.getServer().getScheduler().cancelTask(this.lastTaskId);
        this.preRoundStarted = false;
    }

    public void startRound() {
        GTC.plugin.getServer().getScheduler().cancelTask(this.lastTaskId);
        this.preRoundStarted = false;
        this.roundStarted = true;
        this.roundSeconds = this.playTime;

        this.world.getPlayers().forEach(player -> {
            GTC.playerList.computeIfPresent(player.getUniqueId(), (k, v) -> v = 0);
            player.teleport(this.roundLocation);
            PlayerInventory.reset(player);
            PlayerScoreboard.set(player);
            player.sendTitle("§3GTC", "Good luck!",  10, 70, 20);
            player.setExp(0.99f);
            player.setLevel(20);
        });

        spawnChicken();

        this.lastTaskId = GTC.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(GTC.plugin, () -> {
            if (world.getPlayers().size() == 0) {
                this.roundStarted = false;
                this.roundSeconds = 0;
                if (this.lastChicken != null && !this.lastChicken.isDead())
                    this.lastChicken.remove();
                GTC.plugin.getServer().getScheduler().cancelTask(this.lastTaskId);
                return;
            }

            if (this.roundSeconds >= 1) {
                this.roundSeconds--;
                for (Player p : world.getPlayers()) {
                    float exp = p.getExp() - (float) 1/this.playTime;
                    p.setExp(Math.max(exp, 0.0f));
                    p.setLevel(this.roundSeconds);
                }
            }

            switch (this.roundSeconds) {
                case 5 -> world.getPlayers().forEach(players -> players.sendMessage("§3GTC §7| §fThe round ends in §a5 seconds§f!"));
                case 0 -> stopRound();
            }
        }, 0, 20);
    }

    public void stopRound() {
        this.roundStarted = false;

        GTC.plugin.getServer().getScheduler().cancelTask(this.lastTaskId);

        Player topPlayer = GTC.plugin.getServer().getPlayer(this.topPlayer);
        String topPlayerName = (topPlayer != null) ? topPlayer.getName() : "null";

        this.world.getPlayers().forEach(player -> {
            player.sendTitle("§3The winner is:", topPlayerName,  10, 70, 20);
            player.sendMessage("§3GTC §7| §eThe player §6" + topPlayerName + " §ewon with §6" + GTC.playerList.get(this.topPlayer) + " §ekill(s)!");

            ScoreboardManager manager = GTC.plugin.getServer().getScoreboardManager();
            if (manager != null)
                player.setScoreboard(manager.getNewScoreboard());

            player.teleport(this.spawnLocation);
            player.getInventory().clear();
            player.setExp(0.0f);
            player.setLevel(0);
        });

        if (this.lastChicken != null && !this.lastChicken.isDead())
            this.lastChicken.remove();
    }

    private static double getRandomDouble(double val1, double val2) {
        double min = Math.min(val1, val2);
        double max = Math.max(val1, val2);

        return (Math.random() * (max - min)) + min;
    }

    public void spawnChicken() {
        double randomX = getRandomDouble(this.chickenLocation.get(0).getX(), this.chickenLocation.get(1).getX());
        double randomY = getRandomDouble(this.chickenLocation.get(0).getY(), this.chickenLocation.get(1).getY());
        double randomZ = getRandomDouble(this.chickenLocation.get(0).getZ(), this.chickenLocation.get(1).getZ());

        if (this.lastChicken != null && !this.lastChicken.isDead())
            this.lastChicken.remove();

        this.lastChicken = world.spawnEntity(new Location(world, randomX, randomY, randomZ), EntityType.CHICKEN);
    }
}