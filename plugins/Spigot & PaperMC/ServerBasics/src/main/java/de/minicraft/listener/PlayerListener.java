package de.minicraft.listener;

import de.minicraft.players.SBPlayerApi;
import de.minicraft.players.SBPlayerData;
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
        if (SBPlayerApi.login(e.getPlayer().getUniqueId())) {
            SBPlayerApi.addAllPerm(e.getPlayer().getUniqueId());
            e.getPlayer().updateCommands();
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (!SBPlayerApi.playerList.containsKey(e.getPlayer().getUniqueId())) return;

        SBPlayerData pData = SBPlayerApi.get(e.getPlayer().getUniqueId());
        if (pData == null) {
            ServerBasics.plugin.getLogger().severe("Player data from [" + e.getPlayer().getName() + "] could not be saved!");
            SBPlayerApi.removeAllPerm(e.getPlayer().getUniqueId());
            SBPlayerApi.logout(e.getPlayer().getUniqueId());
            return;
        }

        SBPlayerApi.saveAll(e.getPlayer().getUniqueId());
        SBPlayerApi.removeAllPerm(e.getPlayer().getUniqueId());
        SBPlayerApi.logout(e.getPlayer().getUniqueId());
    }
}