package de.minicraft.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.minicraft.players.playerApi;
import de.minicraft.players.playerPermissions;

public class player implements Listener {
    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent e) {}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        playerApi.addUser(e.getPlayer().getUniqueId());
        playerPermissions.add(e.getPlayer().getUniqueId());
        e.getPlayer().sendMessage("This is a test server!");
        e.getPlayer().sendMessage("§3§l[§2SERVER§3§l] §aYour language has been set to: " + playerApi.getPlayer(e.getPlayer().getUniqueId()).language + ". You can set your language with /setlanguage <lang>");
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (!playerApi.exists(e.getPlayer().getUniqueId())) return;
        playerApi.getPlayer(e.getPlayer().getUniqueId()).saveAll();
        playerPermissions.removeAll(e.getPlayer().getUniqueId());
        playerApi.remove(e.getPlayer().getUniqueId());
    }
}