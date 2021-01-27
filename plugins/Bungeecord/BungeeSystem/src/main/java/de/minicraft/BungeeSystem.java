package de.minicraft;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.minicraft.commands.hub;
import de.minicraft.listener.pluginMessageReceiver;
import de.minicraft.listener.tabList;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

public final class BungeeSystem extends Plugin {
    public static BungeeSystem plugin;

    @Override
    public void onEnable() {
        plugin = this;

        try {
            // Language
            {
                File file = new File(getDataFolder().getPath(), "language.yml");
                if (!file.exists()) {
                    super.getLogger().severe(">>>>> language.yml existiert nicht <<<<<");
                    ProxyServer.getInstance().stop();
                    return;
                }

                config.language = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
            }
        } catch (IOException e) {
            e.getStackTrace();
            ProxyServer.getInstance().stop();
            return;
        }

        // Listener
        plugin.getProxy().getPluginManager().registerListener(plugin, new pluginMessageReceiver());
        plugin.getProxy().getPluginManager().registerListener(plugin, new tabList());

        // Commands
        plugin.getProxy().getPluginManager().registerCommand(plugin, new hub());

        // Channels
        plugin.getProxy().registerChannel("lobby:getserverinfo");
    }

    public static void getServerStatus(ProxiedPlayer p, String server) {
        Collection<ProxiedPlayer> networkPlayers = plugin.getProxy().getPlayers();
        if (networkPlayers == null || networkPlayers.isEmpty()) return;

        boolean serverStatus = true;

        try {
            Socket s = new Socket();
            s.connect(plugin.getProxy().getServerInfo(server).getSocketAddress(), 20);
            s.close();
        } catch (IOException e) {
            serverStatus = false;
        }

        plugin.getLogger().warning("[" + server + "] Status: " + serverStatus);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("getServerStatus");
        out.writeUTF(server);
        out.writeBoolean(serverStatus);

        p.getServer().getInfo().sendData("lobby:getserverinfo", out.toByteArray());
    }
}
