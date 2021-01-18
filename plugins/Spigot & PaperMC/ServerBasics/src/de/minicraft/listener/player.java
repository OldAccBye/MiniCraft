package de.minicraft.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.minicraft.players.playerApi;
import de.minicraft.players.playerPermissions;

public class player implements Listener {
    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent e) {}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        playerPermissions.add(e.getPlayer().getUniqueId());
        playerApi.add(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        playerApi.getPlayer(e.getPlayer().getUniqueId()).saveAll();
        playerPermissions.removeAll(e.getPlayer().getUniqueId());
        playerApi.remove(e.getPlayer().getUniqueId());
    }
}