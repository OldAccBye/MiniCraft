package de.minicraft.lobby;

import de.minicraft.lobby.listener.navigator;
import de.minicraft.lobby.listener.player;
import org.bukkit.plugin.java.JavaPlugin;

public class lobby extends JavaPlugin {
    public static lobby plugin;

    @Override
    public void onEnable() {
        plugin = this;

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getPluginManager().registerEvents(new player(), this);
        getServer().getPluginManager().registerEvents(new navigator(), this);
    }

    @Override
    public void onDisable() {}
}
