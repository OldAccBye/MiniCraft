package de.minicraft;

import de.minicraft.commands.hub;
import de.minicraft.listener.tabList;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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

        ProxyServer.getInstance().getPluginManager().registerListener(this, new tabList());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new hub());
    }
}
