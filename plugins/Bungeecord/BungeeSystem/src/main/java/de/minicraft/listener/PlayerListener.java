package de.minicraft.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.minicraft.BungeeSystem;
import de.minicraft.Utils;
import de.minicraft.player.PlayerApi;
import de.minicraft.player.PlayerData;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener {
    @EventHandler
    public void onServerConnect(ServerConnectEvent e) {
        ProxiedPlayer p = e.getPlayer();

        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
        if (pData == null) {
            p.disconnect(new TextComponent("Ein Fehler ist aufgetreten! Bitte versuche dich neu anzumelden."));
            return;
        }

        ServerInfo serverInfo = e.getTarget();

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("login"); // Option
        out.writeUTF(pData.data.getString("group"));
        out.writeInt(pData.data.getInteger("cookies"));
        if (serverInfo.getName().contains("FFA")) {
            if (pData.ffaData == null) {
                Document playerDoc = PlayerApi.getData(p, "ffa");

                if (playerDoc == null) {
                    e.setCancelled(true);
                    p.disconnect(new TextComponent("[01][ffaData] Irgendetwas ist schief gelaufen!"));
                    return;
                }

                pData.ffaData = playerDoc;
            }

            // FFA DATA WRITE
            out.writeInt(pData.ffaData.getInteger("kills"));
            out.writeInt(pData.ffaData.getInteger("deaths"));
        } else if (serverInfo.getName().contains("GTC")) {
            if (pData.gtcData == null) {
                Document playerDoc = PlayerApi.getData(p, "gtc");

                if (playerDoc == null) {
                    e.setCancelled(true);
                    p.disconnect(new TextComponent("[01][gtcData] Irgendetwas ist schief gelaufen!"));
                    return;
                }

                pData.gtcData = playerDoc;
            }

            // GTC DATA WRITE
            out.writeInt(pData.gtcData.getInteger("won"));
        }
        e.getTarget().sendData("bungeesystem:miniapi", out.toByteArray());
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();

        if (BungeeSystem.playerList.containsKey(p.getUniqueId())) {
            p.disconnect(new TextComponent("Du kannst dich erst 2 Sekunden nachdem du den Server verlassen hast wieder anmelden!"));
            return;
        }

        if (!PlayerApi.login(p)) {
            p.disconnect(new TextComponent("[01][data] Irgendetwas ist schief gelaufen!"));
            return;
        }

        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());
        if (pData.data.getBoolean("banned")) {
            Long currentDateTime = new Date().getTime();

            if (pData.data.getLong("banExpiresTimestamp") > currentDateTime) {
                p.disconnect(new TextComponent("§cDu wurdest aus diesem Netzwerk ausgeschlossen." +
                        "\n\n§cSeit §7>>§f " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(pData.data.getLong("banSinceTimestamp")) +
                        "\n§cAufhebung in §7>>§f " + Utils.convertTimestamp(currentDateTime, pData.data.getLong("banExpiresTimestamp")) +
                        "\n§cBegründung §7>>§f " + pData.data.getString("banReason") +
                        "\n§cAusgeschlossen von §7>>§f " + pData.data.getString("bannedFrom")));
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        // Soll nach 2 Sekunden ausgeführt
        BungeeSystem.plugin.getProxy().getScheduler().schedule(BungeeSystem.plugin, () -> {
            if (!BungeeSystem.playerList.containsKey(e.getPlayer().getUniqueId())) return;
            PlayerApi.saveAll(e.getPlayer().getUniqueId());
            BungeeSystem.playerList.remove(e.getPlayer().getUniqueId());
        }, 2, TimeUnit.SECONDS);
    }
}
