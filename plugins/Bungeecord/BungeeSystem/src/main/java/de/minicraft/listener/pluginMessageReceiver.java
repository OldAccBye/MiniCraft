package de.minicraft.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.minicraft.BungeeSystem;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class PluginMessageReceiver implements Listener {
    @EventHandler
    public void onPluginMessage(PluginMessageEvent e) {
        if (!Arrays.asList("bungeesystem:server").contains(e.getTag())) return;

        if (!(e.getReceiver() instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) e.getReceiver();

        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String subChannel = in.readUTF();

        switch (e.getTag()) {
            case "bungeesystem:server" -> { // LOBBY -> SERVER
                switch (subChannel) { // SERVER (SWITCH)
                    case "connect" -> {
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
            }
        }
    }
}