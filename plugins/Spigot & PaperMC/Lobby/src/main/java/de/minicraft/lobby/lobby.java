package de.minicraft.lobby;

import de.minicraft.lobby.listener.PlayerListener;
import org.bukkit.plugin.java.JavaPlugin;

public class Lobby extends JavaPlugin {
    public static Lobby plugin;

    @Override
    public void onEnable() {
        plugin = this;

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "bungeesystem:server");
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {}
}
