package de.minicraft.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.minicraft.BungeeSystem;
import de.minicraft.Utils;
import de.minicraft.player.PlayerApi;
import de.minicraft.player.PlayerData;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerListener implements Listener {
    @EventHandler
    public void onServerConnect(ServerConnectEvent e) {
        ProxiedPlayer p = e.getPlayer();

        if (!BungeeSystem.playerList.containsKey(p.getUniqueId())) {
            Document playerLoginDoc = PlayerApi.login(p.getUniqueId());

            if (playerLoginDoc.getString("status").equals("error")) {
                e.setCancelled(true);
                p.disconnect(new TextComponent(playerLoginDoc.getString("reason")));
                return;
            }
        }

        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
        if (pData.banned) {
            Long currentDateTime = new Date().getTime();

            if (pData.banExpiresTimestamp > currentDateTime) {
                e.setCancelled(true);
                p.disconnect(new TextComponent("§cDu wurdest von diesem Netzwerk ausgeschlossen." +
                        "\n\n§cSeit §7>>§f " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(pData.banSinceTimestamp) +
                        "\n§cAufhebung in §7>>§f " + Utils.convertTimestamp(currentDateTime, pData.banExpiresTimestamp) +
                        "\n§cBegründung §7>>§f " + pData.banReason +
                        "\n§cAusgeschlossen von §7>>§f " + pData.bannedFrom));
                return;
            }
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Login");
        out.writeUTF(pData.group);
        out.writeInt(pData.cookies);
        e.getTarget().sendData("bungeesystem:player", out.toByteArray());
    }
}
