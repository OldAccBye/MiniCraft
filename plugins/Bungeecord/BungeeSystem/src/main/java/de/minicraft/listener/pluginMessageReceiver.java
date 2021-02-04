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

public class PluginMessageReceiver implements Listener {
    @EventHandler
    public void on(PluginMessageEvent e) {
        if (e.getTag().equalsIgnoreCase("lobby:getserverinfo")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
            String subChannel = in.readUTF();

            if (subChannel.equalsIgnoreCase("getServerStatus")) {
                if (!(e.getReceiver() instanceof ProxiedPlayer)) return;
                ProxiedPlayer p = (ProxiedPlayer) e.getReceiver();

                ServerInfo server = BungeeSystem.plugin.getProxy().getServerInfo(in.readUTF());
                if (server == null) {
                    p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aServer offline!"));
                    return;
                }

                try {
                    Socket s = new Socket();
                    s.connect(server.getSocketAddress(), 15);
                    s.close();
                } catch (IOException err) {
                    p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aNo connection to the server..."));
                    return;
                }

                p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aConnecting to §6server§a..."));
                p.connect(server);
            }
        }
    }
}