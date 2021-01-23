package de.minicraft.listener;

import de.minicraft.players.playerData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.minicraft.players.playerApi;

public class player implements Listener {
    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent e) {}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (playerApi.login(e.getPlayer().getUniqueId())) {
            playerApi.addAllPerm(e.getPlayer().getUniqueId());
            e.getPlayer().updateCommands();
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (!playerApi.playerList.containsKey(e.getPlayer().getUniqueId())) return;

        playerData pData = playerApi.get(e.getPlayer().getUniqueId());
        if (pData == null) {
            Bukkit.getLogger().severe("Player data from [" + e.getPlayer().getName() + "] could not be saved!");
            playerApi.removeAllPerm(e.getPlayer().getUniqueId());
            playerApi.logout(e.getPlayer().getUniqueId());
            return;
        }

        pData.saveAll();
        playerApi.removeAllPerm(e.getPlayer().getUniqueId());
        playerApi.logout(e.getPlayer().getUniqueId());
    }
}