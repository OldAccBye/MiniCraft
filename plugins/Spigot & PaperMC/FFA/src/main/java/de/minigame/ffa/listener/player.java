package de.minigame.ffa.listener;

import de.minigame.ffa.players.playerData;
import de.minigame.ffa.spawnData;
import de.minigame.ffa.players.inventory;
import de.minigame.ffa.players.playerApi;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.*;

public class player implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!playerApi.login(e.getPlayer().getUniqueId())) {
            p.kickPlayer("");
            return;
        }

        p.teleport(spawnData.loc);
        p.sendTitle("§3FFA", "§aWelcome!", 10, 70, 20);
        inventory.getPlayerStandard(p);
        for (Player otherP : Bukkit.getOnlinePlayers())
            createBoard(otherP);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        playerApi.logout(e.getPlayer().getUniqueId());

        for (Player p : Bukkit.getOnlinePlayers())
            if (p != e.getPlayer())
                createBoard(p);
    }

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) { e.setCancelled(true); }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        playerData pData = playerApi.playerList.get(p.getUniqueId());
        pData.deaths += 1;
        pData.killstreak = 0;

        Player k = e.getEntity().getKiller();
        if (k == null)
            e.setDeathMessage("§eThe player §6" + p.getName() + " §emade a mistake :O");
        else if (k.getName().equals(p.getName()))
            e.setDeathMessage("§eThe player §6" + p.getName() + " §ekilled himself :O");
        else {
            e.setDeathMessage("§eThe player §6" + p.getName() + " §ewas killed by §6" + k.getName() + "§e!");
            k.playSound(k.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 10);
            playerData kData = playerApi.playerList.get(k.getUniqueId());
            kData.kills += 1;
            kData.killstreak += 1;
            if (kData.killstreak > 4)
                for (Player others : Bukkit.getOnlinePlayers())
                    if (others != null)
                        others.sendMessage("§ePlayer §6" + k.getName() + " §has a kill streak of §6" + kData.killstreak + "§e!");
            k.setHealth(20);
            createBoard(k);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();

        e.setRespawnLocation(spawnData.loc);
        inventory.getPlayerStandard(p);
        createBoard(p);
        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        Location pLoc = e.getEntity().getLocation();
        Location kLoc = e.getDamager().getLocation();

        double[] x = { -3.158, -8.802 },
                z = { 3.233, 8.862 };

        if ((kLoc.getX() <= x[0] && kLoc.getX() >= x[1]) && (kLoc.getZ() >= z[0] && kLoc.getZ() <= z[1]) ||
                (pLoc.getX() <= x[0] && pLoc.getX() >= x[1]) && (pLoc.getZ() >= z[0] && pLoc.getZ() <= z[1])) {
            e.setCancelled(true);
            return;
        }

        /*// X: -7.715 8.680 | Y: -3.377 3.526
        if (pLoc.getY() > (spawnData.loc.getY() - 1)) {
            e.setCancelled(true);
            return;
        }*/

        Entity damager = e.getDamager();

        if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            if (arrow.getShooter() instanceof Player) {
                Player k = (Player) arrow.getShooter(), p = (Player) e.getEntity();
                p.damage(p.getHealth(), k);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL)
            e.setCancelled(true);
    }

    public void createBoard(Player p) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) return;
        Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("FFA-ScoreBoard", "dummy", "§8>> §cFFA §8<<");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.getScore("§r§e=-=-=-=-=-=-=-=").setScore(4);
        obj.getScore("§a§lKills: §r§6" + playerApi.playerList.get(p.getUniqueId()).kills).setScore(3);
        obj.getScore("§c§lDeaths: §r§6" + playerApi.playerList.get(p.getUniqueId()).deaths).setScore(2);
        obj.getScore("§f§lPlayers: §r§6" + Bukkit.getOnlinePlayers().size()).setScore(1);
        obj.getScore("§e=-=-=-=-=-=-=-=§r").setScore(0);
        p.setScoreboard(board);
    }
}
