package de.minigame.gtc.listener;

import de.minigame.gtc.GTC;
import de.minigame.gtc.players.playerApi;
import de.minigame.gtc.players.scoreboard;
import de.minigame.gtc.worldData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
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
            p.sendTitle("§3GTC", "§aWillkommen!", 10, 70, 20);

            if (!world.preRoundStarted && Bukkit.getServer().getOnlinePlayers().size() >= world.playersToStart)
                GTC.startPreRound(world.worldName);

            return;
        }

        p.kickPlayer("Keine freie Runde momentan verfügbar!");
    }

    @EventHandler
    public static void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String worldName = p.getWorld().getName();
        worldData wData = GTC.worldLists.get(worldName);
        playerApi.logout(e.getPlayer().getUniqueId());

        if (wData.preRoundStarted) {
            if (Bukkit.getServer().getOnlinePlayers().size() < wData.playersToStart) {
                GTC.stopPreRound(worldName);

                World w = e.getPlayer().getWorld();
                for (Player t : w.getPlayers())
                    t.sendMessage("§eAuf weitere Mitspieler warten... §6" + w.getPlayers().size() + "§e/§6" + wData.playersToStart);
            }
        }
    }

    @EventHandler
    public static void onHitPlayer(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public static void onKillEntity(EntityDeathEvent e) {
        e.getDrops().clear();

        Entity mob = e.getEntity();
        if (!mob.getType().equals(EntityType.CHICKEN)) return;

        Player killer = e.getEntity().getKiller();
        if (killer != null) {
            World w = killer.getWorld();
            if (mob != GTC.worldLists.get(w.getName()).lastChicken) return;

            playerApi.playerList.get(killer.getUniqueId()).kills += 1;
            for (Player t : w.getPlayers()) {
                if (playerApi.playerList.get(t.getUniqueId()).inRound) {
                    t.sendMessage("§eDer Spieler §6" + killer.getName() + " §ehat ein Huhn getötet!");
                    scoreboard.set(t);
                }
            }
        }

        GTC.spawnChicken(e.getEntity().getWorld().getName());
    }
}