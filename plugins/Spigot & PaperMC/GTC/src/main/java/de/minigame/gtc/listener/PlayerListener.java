package de.minigame.gtc.listener;

import de.minigame.gtc.GTC;
import de.minigame.gtc.players.PlayerScoreboard;
import de.minigame.gtc.WorldData;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class PlayerListener implements Listener {
    @EventHandler
    public static void onJoin(PlayerJoinEvent e) {
        org.bukkit.entity.Player p = e.getPlayer();

        for (Map.Entry<String, WorldData> data : GTC.worldLists.entrySet()) {
            String key = data.getKey();
            WorldData value = data.getValue();
            if (value.players >= value.maxPlayers || value.roundStarted) continue;

            GTC.playerList.put(p.getUniqueId(), 0);
            p.teleport(value.spawnLocation);
            PlayerScoreboard.set(p);
            p.sendTitle("§3GTC", "§aWillkommen!", 10, 70, 20);

            if (!value.preRoundStarted && GTC.plugin.getServer().getOnlinePlayers().size() >= value.playersToStart)
                GTC.worldLists.get(key).startPreRound();
            else if (!value.preRoundStarted)
                value.world.getPlayers().forEach(players -> players.sendMessage("§3GTC §7| §6" + value.world.getPlayers().size() + "§e/§6" + value.playersToStart + " §eSpieler bis zum Start!"));

            return;
        }

        p.kickPlayer("Keine freie Runde gefunden! :(");
    }

    @EventHandler
    public static void onQuit(PlayerQuitEvent e) {
        org.bukkit.entity.Player p = e.getPlayer();
        String worldName = p.getWorld().getName();
        WorldData wData = GTC.worldLists.get(worldName);
        GTC.playerList.remove(p.getUniqueId());

        if (wData.preRoundStarted) {
            if (wData.world.getPlayers().size() < wData.playersToStart) {
                GTC.worldLists.get(worldName).stopPreRound();
                wData.world.getPlayers().forEach(players -> players.sendMessage("§3GTC §7| §6" + wData.world.getPlayers().size() + "§e/§6" + wData.playersToStart + " §eSpieler bis zum Start!"));
            }
        }
    }

    @EventHandler
    public static void onHitPlayer(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof org.bukkit.entity.Player) || !(e.getDamager() instanceof org.bukkit.entity.Player)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public static void onKillEntity(EntityDeathEvent e) {
        e.getDrops().clear();

        Entity mob = e.getEntity();
        if (!mob.getType().equals(EntityType.CHICKEN)) return;

        org.bukkit.entity.Player killer = e.getEntity().getKiller();
        if (killer != null) {
            World w = killer.getWorld();
            if (mob != GTC.worldLists.get(w.getName()).lastChicken) return;

            GTC.playerList.computeIfPresent(killer.getUniqueId(), (k, v) -> v += 1);
            w.getPlayers().forEach(players -> {
                players.sendMessage("§3GTC §7| §eDer Spieler §6" + killer.getName() + " §ehat ein Huhn getötet!");
                PlayerScoreboard.set(players);
            });
        }

        GTC.worldLists.get(e.getEntity().getWorld().getName()).spawnChicken();
    }
}