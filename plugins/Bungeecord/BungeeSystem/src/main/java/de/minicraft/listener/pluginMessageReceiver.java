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
        if (!Arrays.asList("lobby:server", "basics:command").contains(e.getTag())) return;

        if (!(e.getReceiver() instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) e.getReceiver();

        ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
        String subChannel = in.readUTF();

        switch (e.getTag()) {
            case "lobby:server" -> { // LOBBY -> GETSERVER
                if (subChannel.equals("connect")) { // GETSERVER -> CONNECT
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
            case "basics:command" -> { // BASICS -> COMMANDS
                switch (subChannel) { // COMMANDS (SWITCH)
                    // COMMAND (SWITCH) -> BROADCAST
                    case "broadcast" -> BungeeSystem.plugin.getProxy().getPlayers().forEach(players -> players.sendMessage(new TextComponent(in.readUTF())));
                    case "tpallhere" -> { // COMMAND (SWITCH) -> TPALL
                        p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler werden teleportiert..."));

                        ServerInfo pServerInfo = p.getServer().getInfo();
                        for (ProxiedPlayer players : BungeeSystem.plugin.getProxy().getPlayers()) {
                            if (players.getName().equals(p.getName())) continue;
                            players.connect(pServerInfo);
                            players.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aDu wurdest teleportiert!"));
                        }
                    }
                    case "tp" -> { // COMMAND (SWITCH) -> TP
                        String p1Name = in.readUTF();
                        ProxiedPlayer p1 = BungeeSystem.plugin.getProxy().getPlayer(p1Name);
                        if (p1 == null) {
                            p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler §6" + p1Name + " §akonnte nicht gefunden werden!"));
                            return;
                        }

                        String p2Name = in.readUTF();
                        ProxiedPlayer p2 = BungeeSystem.plugin.getProxy().getPlayer(p2Name);
                        if (p2 == null) {
                            p.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aSpieler §6" + p2Name + " §akonnte nicht gefunden werden!"));
                            return;
                        }

                        p1.connect(p2.getServer().getInfo());
                        p1.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §aDu wurdest zu §6" + p2Name + " §ateleportiert!"));
                        p2.sendMessage(new TextComponent("§3§l[§2SERVER§3§l] §6" + p1Name + " §awurde zu dir teleportiert!"));
                    }
                }
            }
        }
    }
}