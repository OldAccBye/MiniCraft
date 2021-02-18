package de.minicraft;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.minicraft.player.PlayerData;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.net.Socket;

public class PluginMessageReceiver implements Listener {
    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if (!(e.getReceiver() instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) e.getReceiver();

        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());

        switch (e.getTag()) {
            case "bungeesystem:lobby" -> { // LOBBY -> SERVER
                if (in.readUTF().equals("Connect")) { // SERVER (SWITCH)
                    ServerInfo server = BungeeSystem.plugin.getProxy().getServerInfo(in.readUTF());
                    if (server == null) {
                        p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aServer existiert nicht!"));
                        return;
                    }

                    try {
                        Socket s = new Socket();
                        s.connect(server.getSocketAddress(), 15);
                        s.close();
                    } catch (IOException err) {
                        p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aKeine Verbindung zum Server..."));
                        return;
                    }

                    p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aVerbindung wird hergestellt..."));
                    p.connect(server);
                }
            }
            case "bungeesystem:miniapi" -> {
                switch (in.readUTF()) {
                    case "Update" -> {
                        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
                        if (pData == null) {
                            p.disconnect(new TextComponent("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden."));
                            return;
                        }

                        switch (in.readUTF()) {
                            case "All" -> {
                                String currentServerName = p.getServer().getInfo().getName().toLowerCase();

                                pData.group = in.readUTF();
                                pData.cookies = in.readInt();
                                // To do: Switch for server
                            }
                        }
                    }
                }
            }
        }
    }
}