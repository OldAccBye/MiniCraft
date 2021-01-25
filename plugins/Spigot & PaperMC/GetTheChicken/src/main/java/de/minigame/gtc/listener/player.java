package de.minigame.gtc.listener;

import de.minigame.gtc.GTC;
import de.minigame.gtc.players.playerApi;
import de.minigame.gtc.players.scoreboard;
import de.minigame.gtc.worldData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class player implements Listener {
    @EventHandler
    public static void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        for (worldData world : GTC.worldLists.values()) {
            if (Bukkit.getServer().getWorld(world.worldName) == null || world.players >= world.maxPlayers || world.roundStarted) continue;

            playerApi.login(p.getUniqueId());
            p.teleport(world.spawnLocation);
            p.sendTitle("§3GTC", "§aWelcome!", 10, 70, 20);
            p.sendMessage(p.getWorld().getName());

            if (!world.preRoundStarted && Bukkit.getServer().getOnlinePlayers().size() >= world.playersToStart)
                GTC.startPreRound(world.worldName);

            return;
        }

        p.kickPlayer("Kein Platz mehr gefunden! :(");
    }

    @EventHandler
    public static void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String worldName = p.getWorld().getName();
        worldData world = GTC.worldLists.get(worldName);
        playerApi.logout(e.getPlayer().getUniqueId());

        if (world.preRoundStarted)
            if (Bukkit.getServer().getOnlinePlayers().size() < world.playersToStart)
                GTC.stopPreRound(worldName);
    }

    @EventHandler
    public static void onKill(EntityDeathEvent e) {
        Entity mob = e.getEntity();
        Player killer = e.getEntity().getKiller();
        if (!mob.getType().equals(EntityType.CHICKEN) || killer == null) return;

        playerApi.playerList.get(killer.getUniqueId()).kills += 1;
        for (Player t : Bukkit.getServer().getOnlinePlayers()) {
            if (playerApi.playerList.get(t.getUniqueId()).inRound) {
                t.sendMessage("§eDer Spieler §6" + killer.getName() + " §ehat ein Huhn getötet!");
                scoreboard.set(t);
            }
        }
        GTC.spawnChicken(killer.getWorld().getName());
    }
}
