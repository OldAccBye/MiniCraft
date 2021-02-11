package de.minicraft.listener;

import de.minicraft.player.PlayerApi;
import de.minicraft.ServerBasics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (ServerBasics.playerList.containsKey(e.getPlayer().getUniqueId()))
            PlayerApi.logout(e.getPlayer().getUniqueId());
    }
}