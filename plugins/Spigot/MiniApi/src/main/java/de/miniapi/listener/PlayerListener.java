package de.miniapi.listener;

import de.miniapi.MiniApi;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) { e.joinMessage(null); }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.quitMessage(null);
        MiniApi.playerList.remove(e.getPlayer().getUniqueId());
    }
}
