package de.minicraft;

import de.minicraft.commands.HubCommand;
import de.minicraft.listener.PluginMessageReceiver;
import de.minicraft.listener.Tablist;
import net.md_5.bungee.api.plugin.Plugin;

public final class BungeeSystem extends Plugin {
    public static BungeeSystem plugin;

    @Override
    public void onEnable() {
        plugin = this;

        // Listener
        plugin.getProxy().getPluginManager().registerListener(plugin, new PluginMessageReceiver());
        plugin.getProxy().getPluginManager().registerListener(plugin, new Tablist());

        // Commands
        plugin.getProxy().getPluginManager().registerCommand(plugin, new HubCommand());

        // Channels
        plugin.getProxy().registerChannel("lobby:server");
        plugin.getProxy().registerChannel("basics:command");
    }
}
