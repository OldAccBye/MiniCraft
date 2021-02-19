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
import java.util.Map;

public class PluginMessageReceiver implements Listener {
    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if (!(e.getReceiver() instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) e.getReceiver();
        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
        if (pData == null) {
            p.disconnect(new TextComponent("Es konnten keine Daten abgerufen werden. Bitte versuche dich neu anzumelden."));
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());

        switch (e.getTag()) {
            case "bungeesystem:lobby" -> { // LOBBY -> SERVER
                if (in.readUTF().equals("Connect")) { // SERVER (SWITCH)
                    String serverName = in.readUTF();
                    int maxPlayers = in.readInt(), serverFound = 0;

                    for (Map.Entry<String, ServerInfo> entry : BungeeSystem.plugin.getProxy().getServersCopy().entrySet()) {
                        if (!entry.getKey().contains(serverName)) continue;
                        ServerInfo value = entry.getValue();
                        if (value.getPlayers().size() >= maxPlayers) {
                            serverFound += 1;
                            continue;
                        }

                        try {
                            Socket s = new Socket();
                            s.connect(value.getSocketAddress(), 15);
                            s.close();
                        } catch (IOException ignored) {
                            continue;
                        }

                        p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aVerbindung wird hergestellt..."));
                        p.connect(value);
                        return;
                    }

                    if (serverFound != 0)
                        p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aAlle Server sind voll!"));
                    else
                        p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aKein Server gefunden!"));
                }
            }
            case "bungeesystem:miniapi" -> {
                switch (in.readUTF()) {
                    case "Update" -> {
                        switch (in.readUTF()) {
                            case "All" -> {
                                String currentServerName = p.getServer().getInfo().getName();

                                pData.group = in.readUTF();
                                pData.cookies = in.readInt();
                                if (currentServerName.contains("FFA")) {
                                    pData.ffaData.kills = in.readInt();
                                    pData.ffaData.deaths = in.readInt();
                                } else if (currentServerName.contains("GTC")) {
                                    pData.gtcData.won = in.readInt();
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}