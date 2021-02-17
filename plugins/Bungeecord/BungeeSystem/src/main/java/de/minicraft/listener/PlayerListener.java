package de.minicraft.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.minicraft.BungeeSystem;
import de.minicraft.Utils;
import de.minicraft.player.PlayerApi;
import de.minicraft.player.PlayerData;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerListener implements Listener {
    @EventHandler
    public void onServerConnect(ServerConnectEvent e) {
        ProxiedPlayer p = e.getPlayer();

        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
        if (pData == null) {
            p.disconnect(new TextComponent("Ein Fehler ist aufgetreten! Bitte versuche dich neu anzumelden."));
            return;
        }

        ServerInfo serverInfo = e.getTarget();
        String serverName = serverInfo.getName().toLowerCase();

        ByteArrayDataOutput out;
        out = ByteStreams.newDataOutput();

        switch (serverName) {
            case "lobby" -> {
                out.writeUTF("Login"); // Option
                out.writeUTF(pData.group);
                e.getTarget().sendData("bungeesystem:minibasics", out.toByteArray());

                out = ByteStreams.newDataOutput();
                out.writeUTF("Login"); // Option
                out.writeUTF(pData.group);
                out.writeInt(pData.cookies);
                e.getTarget().sendData("bungeesystem:lobby", out.toByteArray());
            }
        }
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();

        Document playerLoginDoc = PlayerApi.login(p.getUniqueId());

        if (playerLoginDoc.getString("status").equals("error")) {
            p.disconnect(new TextComponent(playerLoginDoc.getString("reason")));
            return;
        }

        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
        if (pData.banned) {
            Long currentDateTime = new Date().getTime();

            if (pData.banExpiresTimestamp > currentDateTime) {
                p.disconnect(new TextComponent("§cDu wurdest aus diesem Netzwerk ausgeschlossen." +
                        "\n\n§cSeit §7>>§f " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(pData.banSinceTimestamp) +
                        "\n§cAufhebung in §7>>§f " + Utils.convertTimestamp(currentDateTime, pData.banExpiresTimestamp) +
                        "\n§cBegründung §7>>§f " + pData.banReason +
                        "\n§cAusgeschlossen von §7>>§f " + pData.bannedFrom));
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        if (!BungeeSystem.playerList.containsKey(e.getPlayer().getUniqueId())) return;
        PlayerApi.saveAll(e.getPlayer().getUniqueId());
        BungeeSystem.playerList.remove(e.getPlayer().getUniqueId());
    }
}
