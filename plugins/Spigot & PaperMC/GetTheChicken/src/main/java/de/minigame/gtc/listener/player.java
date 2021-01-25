package de.minigame.gtc.listener;

import de.minigame.gtc.GTC;
import de.minigame.gtc.players.playerApi;
import de.minigame.gtc.players.scoreboard;
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

        playerApi.login(p.getUniqueId());
        p.teleport(GTC.spawnLocation);
        p.sendTitle("§3GTC", "§aWelcome!", 10, 70, 20);

        if (GTC.roundStarted)
            p.sendMessage("Die Runde hat bereits angefangen! Bitte warte bis die Runde beendet wurde...");
        else if (Bukkit.getOnlinePlayers().size() >= 6)
            GTC.startPreRound();
    }

    @EventHandler
    public static void onQuit(PlayerQuitEvent e) {
        playerApi.logout(e.getPlayer().getUniqueId());

        if (GTC.preRoundStarted)
            if (Bukkit.getOnlinePlayers().size() < 6)
                GTC.stopPreRound();
    }

    @EventHandler
    public static void onKill(EntityDeathEvent e) {
        Entity mob = e.getEntity();
        Player killer = e.getEntity().getKiller();
        if (mob.getType().equals(EntityType.CHICKEN) && killer != null) {
            playerApi.playerList.get(killer.getUniqueId()).kills += 1;
            for (Player t : Bukkit.getOnlinePlayers()) {
                if (playerApi.playerList.get(t.getUniqueId()).inRound) {
                    t.sendMessage("§eDer Spieler §6" + killer.getName() + " §ehat ein Huhn getötet!");
                    scoreboard.set(t);
                    GTC.spawnChicken();
                }
            }
        }
    }
}
