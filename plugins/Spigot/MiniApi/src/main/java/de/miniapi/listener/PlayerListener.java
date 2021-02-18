package de.miniapi.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.miniapi.Configs;
import de.miniapi.MiniApi;
import de.miniapi.player.PlayerData;
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
        PlayerData pData = MiniApi.playerList.get(e.getPlayer().getUniqueId());
        if (pData != null) {
            { // Send all data to Bungeecord
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Update");
                out.writeUTF("All");
                out.writeUTF(pData.group);
                out.writeInt(pData.cookies);
                switch (Configs.serverName) {
                    case "FFA" -> {
                        out.writeInt(pData.ffaData.kills);
                        out.writeInt(pData.ffaData.deaths);
                    }
                    case "GTC" -> out.writeInt(pData.gtcData.won);
                }
                e.getPlayer().sendPluginMessage(MiniApi.plugin, "bungeesystem:miniapi", out.toByteArray());
            }

            MiniApi.playerList.remove(e.getPlayer().getUniqueId());
        }
    }
}
