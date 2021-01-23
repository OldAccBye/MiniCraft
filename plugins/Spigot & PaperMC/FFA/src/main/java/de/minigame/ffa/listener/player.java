package de.minigame.ffa.listener;

import de.minigame.ffa.data;
import de.minigame.ffa.players.inventory;
import de.minigame.ffa.players.playerApi;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;

public class player implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (!playerApi.login(e.getPlayer().getUniqueId())) {
            p.kickPlayer("");
            return;
        }

        if (data.spawnPoint) {
            p.teleport(data.loc);
            p.sendTitle("§3FFA", "§aWelcome!", 10, 70, 20);
            inventory.getPlayerStandard(p);
            for (Player otherP : Bukkit.getOnlinePlayers())
                createBoard(otherP);
        } else if (p.hasPermission("minigames.commands.setspawn")) {
            p.setGameMode(GameMode.CREATIVE);
        }
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
        Player k = e.getEntity().getKiller();

        if (k == null)
            e.setDeathMessage("§eDer Spieler §6" + p.getName() + " §ehat einen Fehler begangen :O");
        else {
            e.setDeathMessage("§eDer Spieler §6" + p.getName() + " §ewurde von §6" + k.getName() + " §ehalbiert!");
            k.playSound(k.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 10);
            playerApi.playerList.get(k.getUniqueId()).kills += 1;
            k.setHealth(20);
            createBoard(k);
        }

        p.teleport(data.loc);
        p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
        playerApi.playerList.get(p.getUniqueId()).deaths += 1;
        createBoard(p);
        inventory.getPlayerStandard(p);
    }

    @EventHandler
    public void onDamageByEntity(EntityDamageByEntityEvent e) {
        Location pLoc = e.getDamager().getLocation();
        // X: -7.715 8.680 | Y: -3.377 3.526
        if (pLoc.getY() > (data.loc.getY() - 1)) {
            e.setCancelled(true);
            return;
        }

        Entity damager = e.getDamager();

        if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            if (arrow.getShooter() instanceof Player) {
                Player k = (Player) arrow.getShooter();
                if (k == null) return;

                Player p = (Player) e.getEntity();
                p.damage(p.getHealth(), k);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Player p = (Player) e.getEntity();

        if (e.getCause() == EntityDamageEvent.DamageCause.FALL)
            e.setCancelled(true);
    }

    public void createBoard(Player p) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
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
