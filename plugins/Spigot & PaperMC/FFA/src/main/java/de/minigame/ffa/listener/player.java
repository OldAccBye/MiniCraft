package de.minigame.ffa.listener;

import de.minigame.ffa.players.playerApi;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class player implements Listener {
    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (!playerApi.login(e.getPlayer().getUniqueId())) {
            e.getPlayer().kickPlayer("");
            return;
        }
    }
}
