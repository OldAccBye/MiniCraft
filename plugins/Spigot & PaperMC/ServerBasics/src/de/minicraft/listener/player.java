package de.minicraft.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.minicraft.players.playerApi;
import de.minicraft.players.playerPermissions;

import java.util.Objects;

public class player implements Listener {
    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent e) {}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        playerApi.add(e.getPlayer().getUniqueId());
        playerPermissions.add(e.getPlayer().getUniqueId());
        e.getPlayer().sendMessage("This is a test server for programming open source Spigot plugins!");
        e.getPlayer().sendMessage("Your language has been set to: " + Objects.requireNonNull(playerApi.getPlayer(e.getPlayer().getUniqueId())).language + ". You can set your language with /setlanguage <lang_lang>");
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (!playerApi.exists(e.getPlayer().getUniqueId())) return;
        Objects.requireNonNull(playerApi.getPlayer(e.getPlayer().getUniqueId())).saveAll();
        playerPermissions.removeAll(e.getPlayer().getUniqueId());
        playerApi.remove(e.getPlayer().getUniqueId());
    }
}