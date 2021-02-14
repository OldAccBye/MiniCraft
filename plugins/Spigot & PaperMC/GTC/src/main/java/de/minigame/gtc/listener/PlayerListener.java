package de.minigame.gtc.listener;

import de.minigame.gtc.GTC;
import de.minigame.gtc.players.PlayerData;
import de.minigame.gtc.players.PlayerScoreboard;
import de.minigame.gtc.WorldData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public static void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        GTC.playerList.put(p.getUniqueId(), new PlayerData());
        p.teleport(WorldData.spawnLocation);
        PlayerScoreboard.set(p);
        p.sendTitle("§3GTC", "§aWillkommen!", 10, 70, 20);

        if (!WorldData.preRoundStarted && GTC.plugin.getServer().getOnlinePlayers().size() >= WorldData.playersToStart)
            WorldData.startPreRound();
        else if (!WorldData.preRoundStarted)
            for (Player players : GTC.plugin.getServer().getOnlinePlayers())
                players.sendMessage("§3GTC §7| §6" + GTC.plugin.getServer().getOnlinePlayers().size() + "§e/§6" + WorldData.playersToStart + " §eSpieler bis zum Start!");
    }

    @EventHandler
    public static void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        GTC.playerList.remove(p.getUniqueId());

        if (!WorldData.preRoundStarted || GTC.plugin.getServer().getOnlinePlayers().size() >= WorldData.playersToStart) return;
        WorldData.stopPreRound();

        for (Player players : GTC.plugin.getServer().getOnlinePlayers())
            players.sendMessage("§3GTC §7| §6" + GTC.plugin.getServer().getOnlinePlayers().size() + "§e/§6" + WorldData.playersToStart + " §eSpieler bis zum Start!");
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
        if (!mob.getType().equals(EntityType.CHICKEN) || mob != WorldData.lastChicken) return;

        Player killer = e.getEntity().getKiller();
        if (killer != null) {
            GTC.playerList.get(killer.getUniqueId()).kills += 1;

            GTC.plugin.getServer().getOnlinePlayers().forEach(players -> {
                players.sendMessage("§3GTC §7| §eSpieler §6" + killer.getName() + " §ehat ein Huhn getötet!");
                PlayerScoreboard.set(players);
            });
        }

        WorldData.spawnChicken();
    }
}