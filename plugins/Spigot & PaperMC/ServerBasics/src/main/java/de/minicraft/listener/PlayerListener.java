package de.minicraft.listener;

import de.minicraft.players.PlayerApi;
import de.minicraft.players.PlayerData;
import de.minicraft.ServerBasics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent e) {}

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (PlayerApi.login(e.getPlayer().getUniqueId())) {
            PlayerApi.addAllPerm(e.getPlayer().getUniqueId());
            e.getPlayer().updateCommands();
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (!PlayerApi.playerList.containsKey(e.getPlayer().getUniqueId())) return;

        PlayerData pData = PlayerApi.get(e.getPlayer().getUniqueId());
        if (pData == null) {
            ServerBasics.plugin.getLogger().severe("Spieler [" + e.getPlayer().getName() + "] konnte nicht gespeichert werden!");
            PlayerApi.removeAllPerm(e.getPlayer().getUniqueId());
            PlayerApi.logout(e.getPlayer().getUniqueId());
            return;
        }

        PlayerApi.saveAll(e.getPlayer().getUniqueId());
        PlayerApi.removeAllPerm(e.getPlayer().getUniqueId());
        PlayerApi.logout(e.getPlayer().getUniqueId());
    }
}