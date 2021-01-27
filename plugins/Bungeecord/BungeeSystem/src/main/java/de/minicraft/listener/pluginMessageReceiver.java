package de.minicraft.listener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import de.minicraft.BungeeSystem;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class pluginMessageReceiver implements Listener {
    @EventHandler
    public void on(PluginMessageEvent e) {
        if (e.getTag().equalsIgnoreCase("lobby:getserverinfo")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());
            String subChannel = in.readUTF();
            if (subChannel.equalsIgnoreCase("getServerStatus")) {
                if (!(e.getReceiver() instanceof ProxiedPlayer)) return;
                BungeeSystem.getServerStatus((ProxiedPlayer) e.getReceiver(), in.readUTF());
            }
        }
    }
}