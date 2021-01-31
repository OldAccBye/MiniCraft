package de.minicraft;

import de.minicraft.commands.hub;
import de.minicraft.listener.pluginMessageReceiver;
import de.minicraft.listener.tabList;
import net.md_5.bungee.api.plugin.Plugin;

public final class BungeeSystem extends Plugin {
    public static BungeeSystem plugin;

    @Override
    public void onEnable() {
        plugin = this;

        // Listener
        plugin.getProxy().getPluginManager().registerListener(plugin, new pluginMessageReceiver());
        plugin.getProxy().getPluginManager().registerListener(plugin, new tabList());

        // Commands
        plugin.getProxy().getPluginManager().registerCommand(plugin, new hub());

        // Channels
        plugin.getProxy().registerChannel("lobby:getserverinfo");
    }
}
