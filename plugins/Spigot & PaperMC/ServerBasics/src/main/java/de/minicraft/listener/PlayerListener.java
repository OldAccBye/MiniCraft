package de.minicraft.listener;

import de.minicraft.player.PlayerApi;
import de.minicraft.ServerBasics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage("");
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.setQuitMessage("");

        if (ServerBasics.playerList.containsKey(e.getPlayer().getUniqueId()))
            PlayerApi.logout(e.getPlayer().getUniqueId());
    }
}