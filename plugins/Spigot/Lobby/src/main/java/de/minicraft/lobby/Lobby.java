package de.minicraft.lobby;

import de.miniapi.MiniApi;
import de.minicraft.lobby.listener.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Lobby extends JavaPlugin {
    public static Lobby plugin;
    public static MiniApi api = (MiniApi) Bukkit.getPluginManager().getPlugin("MiniApi");

    @Override
    public void onEnable() {
        plugin = this;

        /* ===== LISTENER - START ===== */
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        /* ===== LISTENER - END ===== */

        /* ===== CHANNELS - START ===== */
        // Outgoing
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(this, "bungeesystem:lobby");
        /* ===== CHANNELS - END ===== */
    }

    @Override
    public void onDisable() {}
}
