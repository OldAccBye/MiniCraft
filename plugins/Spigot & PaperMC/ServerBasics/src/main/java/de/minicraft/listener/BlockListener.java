package de.minicraft.listener;

import de.minicraft.SBConfig;
import de.minicraft.players.SBPlayerData;
import de.minicraft.players.SBPlayerApi;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Date;

public class BlockListener implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();

        SBPlayerData pData = SBPlayerApi.get(p.getUniqueId());
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
                pData.blockBreakEventTimestamp = currentDateTime;
                pData.blockBreakEventCounter = 0;
                return;
            }

            pData.blockBreakEventCounter += 1;

            if (pData.blockBreakEventCounter >= 20) {
                pData.banned = true;
                pData.banSinceTimestamp = currentDateTime;
                pData.banExpiresTimestamp = (currentDateTime + 120000);
                pData.banReason = "Spamming";
                pData.bannedFrom = "SYSTEM";
                p.kickPlayer("BANNED!");
                return;
            }

            if ((pData.blockBreakEventCounter > 10) && (pData.blockBreakEventCounter <= 15))
                p.sendMessage(SBConfig.getLanguageText(p.getUniqueId(), "doNotDoThat"));
            else if (pData.blockBreakEventCounter > 15)
                p.sendMessage(SBConfig.getLanguageText(p.getUniqueId(), "youCanGetBanned"));
        }
    }
}