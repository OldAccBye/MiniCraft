package de.miniapi;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.miniapi.listener.PlayerListener;
import de.miniapi.player.PlayerData;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class MiniApi extends JavaPlugin {
    public static MiniApi plugin;
    public static final HashMap<UUID, PlayerData> playerList = new HashMap<>();

    @Override
    public void onEnable() {
        plugin = this;
        File file;

        if (!getDataFolder().exists() && !getDataFolder().mkdir()) {
            plugin.getLogger().severe("Es konnte kein Ordner für dieses Plugin erstellt werden!");
            plugin.getServer().shutdown();
            return;
        }

        // Load permissions
        {
            file = new File(getDataFolder().getPath(), "permissions.yml");
            if (!file.exists()) {
                plugin.getLogger().severe("Es konnte keine permissions.yml geladen werden!");
                plugin.getServer().shutdown();
            }

            Configs.permissionsList = YamlConfiguration.loadConfiguration(file);
        }

        // Load config
        {
            file = new File(getDataFolder().getPath(), "config.yml");
            if (!file.exists()) {
                plugin.getLogger().severe("Es konnte keine config.yml geladen werden!");
                plugin.getServer().shutdown();
            }

            Configuration cfg = YamlConfiguration.loadConfiguration(file);
            String serverName = cfg.getString("server");
            Configs.serverName = serverName != null ? serverName.toLowerCase() : "default";
        }

        /* ===== LISTENER - START ===== */
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
        /* ===== LISTENER - END ===== */

        /* ===== CHANNELS - START ===== */
        // Incoming
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "bungeesystem:miniapi", new PluginMessageReceiver());

        // Outgoing
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "bungeesystem:miniapi");
        /* ===== CHANNELS - END ===== */
    }

    public PlayerData getPlayer(UUID pUUID) {
        return playerList.get(pUUID);
    }

    public boolean savePlayer(Player p) {
        PlayerData pData = playerList.get(p.getUniqueId());
        if (pData == null) return false;

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("save");
        switch (Configs.serverName) {
            case "ffa" -> {
                if (pData.ffaData == null) return false;
                out.writeInt(pData.ffaData.kills);
                out.writeInt(pData.ffaData.deaths);
            }
            case "gtc" -> {
                if (pData.gtcData == null) return false;
                out.writeInt(pData.gtcData.won);
            }
        }
        out.writeUTF(pData.group);
        out.writeInt(pData.cookies);
        p.sendPluginMessage(MiniApi.plugin, "bungeesystem:miniapi", out.toByteArray());

        return true;
    }
}
