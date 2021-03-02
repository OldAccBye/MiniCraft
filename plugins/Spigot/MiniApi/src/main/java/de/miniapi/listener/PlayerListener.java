package de.miniapi.listener;

import de.miniapi.MiniApi;
import de.miniapi.player.PlayerApi;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e) {
        Player p = e.getPlayer();

        if (!PlayerApi.login(p))
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Component.text("[ma-opl-01] Die Anmeldung auf diesem Server ist schief gelaufen. Melde dies im Support, sollte dieser Fehler erneut auftauchen."));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.joinMessage(null);
        PlayerApi.checkPremium(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.quitMessage(null);
        PlayerApi.saveAll(e.getPlayer().getUniqueId());
        MiniApi.playerList.remove(e.getPlayer().getUniqueId());
    }
}
