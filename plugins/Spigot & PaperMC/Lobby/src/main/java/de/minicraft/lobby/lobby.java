package de.minicraft.lobby;

import de.minicraft.lobby.listener.PlayerListener;
import de.minicraft.lobby.player.PlayerData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class Lobby extends JavaPlugin {
    public static Lobby plugin;
    public final static HashMap<UUID, PlayerData> playerList = new HashMap<>();

    @Override
    public void onEnable() {
        plugin = this;

        /* ===== LISTENER - START ===== */
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        /* ===== LISTENER - END ===== */

        /* ===== CHANNELS - START ===== */
        // Incoming
        plugin.getServer().getMessenger().registerIncomingPluginChannel(this, "bungeesystem:lobby", new PluginMessageReceiver());

        // Outgoing
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(this, "bungeesystem:lobby");
        /* ===== CHANNELS - END ===== */
    }

    @Override
    public void onDisable() {}
}
