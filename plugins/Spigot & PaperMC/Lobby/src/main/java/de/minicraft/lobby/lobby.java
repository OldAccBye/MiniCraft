package de.minicraft.lobby;

import de.minicraft.lobby.listener.navigator;
import de.minicraft.lobby.listener.player;
import de.minicraft.lobby.listener.pluginMessageReceived;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class lobby extends JavaPlugin {
    public static HashMap<String, lobbyData> lobbyLists = new HashMap<>();
    public static lobby plugin;

    @Override
    public void onEnable() {
        plugin = this;

        lobbyLists.put("FFA", new lobbyData());
        lobbyLists.put("GTC", new lobbyData());

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "lobby:getserverinfo");
        getServer().getMessenger().registerIncomingPluginChannel(this, "lobby:getserverinfo", new pluginMessageReceived());
        getServer().getPluginManager().registerEvents(new player(), this);
        getServer().getPluginManager().registerEvents(new navigator(), this);
    }

    @Override
    public void onDisable() {}
}
