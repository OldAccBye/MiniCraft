package de.minicraft.listener;

import com.mongodb.client.model.Filters;
import de.minicraft.BungeeSystem;
import de.minicraft.Utils;
import de.minicraft.player.PlayerApi;
import de.minicraft.player.PlayerData;
import net.md_5.bungee.api.chat.TextComponent;
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
    public void onPostLogin(PostLoginEvent e) {
        ProxiedPlayer p = e.getPlayer();

        if (BungeeSystem.playerList.containsKey(p.getUniqueId())) {
            p.disconnect(new TextComponent("Bitte habe ein wenig Geduld bis du dich erneut anmelden kannst."));
            return;
        }

        Document banCheck = BungeeSystem.mongo.banned.find(Filters.eq("UUID", p.getUniqueId().toString())).first();

        if (banCheck != null) {
            Long currentDateTime = new Date().getTime();

            if (banCheck.getLong("banExpiresTimestamp") > currentDateTime) {
                p.disconnect(new TextComponent("§cDu wurdest aus diesem Netzwerk ausgeschlossen." +
                        "\n\n§cSeit §7>>§f " + new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(banCheck.getLong("banSinceTimestamp")) +
                        "\n§cAufhebung in §7>>§f " + Utils.convertTimestamp(currentDateTime, banCheck.getLong("banExpiresTimestamp")) +
                        "\n§cBegründung §7>>§f " + banCheck.getString("banReason") +
                        "\n§cAusgeschlossen von §7>>§f " + banCheck.getString("bannedFrom")));
                return;
            }
        }

        if (!PlayerApi.login(p)) {
            p.disconnect(new TextComponent("[b-opl-01] Es konnten keine Spielerdaten gefunden werden. Melde dies im Support, sollte dieser Fehler erneut auftauchen."));
            return;
        }

        PlayerData pData = BungeeSystem.playerList.get(p.getUniqueId());

        if (BungeeSystem.plugin.getProxy().getOnlineCount() > BungeeSystem.plugin.getProxy().getConfigurationAdapter().getListeners().iterator().next().getMaxPlayers())
            if (pData.data.getString("group").equals("default"))
                p.disconnect(new TextComponent("Du musst Premium, Donator oder ein Teammitglied sein um über dem Limit der Spieleranzahl beitreten zu können."));
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent e) {
        if (!BungeeSystem.playerList.containsKey(e.getPlayer().getUniqueId())) return;
        PlayerApi.saveAll(e.getPlayer().getUniqueId());

        // Spielerdaten werden nach 2 Sekunden entfernt
        BungeeSystem.plugin.getProxy().getScheduler().schedule(BungeeSystem.plugin, () -> {
            BungeeSystem.playerList.remove(e.getPlayer().getUniqueId());
        }, 2, TimeUnit.SECONDS);
    }
}
