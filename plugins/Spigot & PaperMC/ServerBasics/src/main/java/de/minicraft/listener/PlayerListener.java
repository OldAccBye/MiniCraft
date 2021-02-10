package de.minicraft.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.minicraft.player.PlayerApi;
import de.minicraft.player.PlayerData;
import de.minicraft.ServerBasics;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (!ServerBasics.playerList.containsKey(e.getPlayer().getUniqueId())) return;

        PlayerData pData = PlayerApi.get(e.getPlayer().getUniqueId());
        if (pData == null) {
            ServerBasics.plugin.getLogger().severe("Spieler [" + e.getPlayer().getName() + "] konnte nicht gespeichert werden!");
            PlayerApi.removeAllPerm(e.getPlayer());
            PlayerApi.logout(e.getPlayer().getUniqueId());
            return;
        }

        PlayerApi.removeAllPerm(e.getPlayer());
        PlayerApi.logout(e.getPlayer().getUniqueId());
    }
}