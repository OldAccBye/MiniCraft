package de.minigame.gtc.listener;

import de.minigame.gtc.GTC;
import de.minigame.gtc.players.scoreboard;
import de.minigame.gtc.worldData;
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

import java.util.Map;

public class player implements Listener {
    @EventHandler
    public static void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        for (Map.Entry<String, worldData> data : GTC.worldLists.entrySet()) {
            String key = data.getKey();
            worldData value = data.getValue();
            if (GTC.plugin.getServer().getWorld(key) == null || value.players >= value.maxPlayers || value.roundStarted) continue;

            GTC.playerList.put(p.getUniqueId(), 0);
            p.teleport(value.spawnLocation);
            p.sendTitle("§3GTC", "§aWillkommen!", 10, 70, 20);

            if (!value.preRoundStarted && GTC.plugin.getServer().getOnlinePlayers().size() >= value.playersToStart)
                GTC.startPreRound(key);

            return;
        }

        p.kickPlayer("Keine freie Runde momentan verfügbar!");
    }

    @EventHandler
    public static void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        String worldName = p.getWorld().getName();
        worldData wData = GTC.worldLists.get(worldName);
        GTC.playerList.remove(p.getUniqueId());

        if (wData.preRoundStarted) {
            if (GTC.plugin.getServer().getOnlinePlayers().size() < wData.playersToStart) {
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

            GTC.playerList.computeIfPresent(killer.getUniqueId(), (k, v) -> v + 1);
            for (Player t : w.getPlayers()) {
                t.sendMessage("§eDer Spieler §6" + killer.getName() + " §ehat ein Huhn getötet!");
                scoreboard.set(t);
            }
        }

        GTC.spawnChicken(e.getEntity().getWorld().getName());
    }
}