package de.minicraft.listener;

import de.minicraft.config;
import de.minicraft.players.playerApi;
import de.minicraft.players.playerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Date;

public class block implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();

        playerData pData = playerApi.get(p.getUniqueId());
        if (pData == null) {
            p.kickPlayer("Player data missing. Try to login again.");
            return;
        }

        if (!p.hasPermission("minicraft.blockbreak")) {
            e.setCancelled(true);

            Date date = new Date();
            long currentDateTime = date.getTime();

            // 3000 = 3 Sekunden
            if (pData.blockBreakEventTimestamp == null
                    || currentDateTime > (pData.blockBreakEventTimestamp + 3000))
            {
                p.sendMessage(config.getLanguageText(p.getUniqueId(), "doNotDoThat"));
                pData.blockBreakEventTimestamp = currentDateTime;
                pData.blockBreakEventCounter = 0;
                return;
            }

            pData.blockBreakEventCounter += 1;

            if (pData.blockBreakEventCounter >= 10) {
                pData.banned = true;
                pData.banSinceTimestamp = currentDateTime;
                pData.banExpiresTimestamp = (currentDateTime + 120000);
                pData.banReason = "Spamming";
                pData.bannedFrom = "SYSTEM";
                p.kickPlayer("BANNED!");
                return;
            }

            if (pData.blockBreakEventCounter < 3)
                p.sendMessage(config.getLanguageText(p.getUniqueId(), "doNotDoThat"));
            else
                p.sendMessage(config.getLanguageText(p.getUniqueId(), "youCanGetBanned"));
        }
    }
}