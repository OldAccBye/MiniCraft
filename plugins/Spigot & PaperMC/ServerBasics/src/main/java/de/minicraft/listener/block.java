package de.minicraft.listener;

import de.minicraft.config;
import de.minicraft.players.playerApi;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Date;

public class block implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("minicraft.blockbreak")) {
            e.setCancelled(true);

            Date date = new Date();
            long currentDateTime = date.getTime();

            // 3000 = 3 Sekunden
            if (playerApi.get(p.getUniqueId()).blockBreakEventTimestamp == null
                    || currentDateTime > (playerApi.get(p.getUniqueId()).blockBreakEventTimestamp + 3000))
            {
                p.sendMessage(config.getLanguageText(p.getUniqueId(), "theyCanBeBanned"));
                playerApi.get(p.getUniqueId()).blockBreakEventTimestamp = currentDateTime;
                playerApi.get(p.getUniqueId()).blockBreakEventCounter = 0;
                return;
            }

            if (playerApi.get(p.getUniqueId()).blockBreakEventCounter < 10) {
                playerApi.get(p.getUniqueId()).blockBreakEventCounter += 1;
                return;
            }

            playerApi.get(p.getUniqueId()).banned = true;
            playerApi.get(p.getUniqueId()).banSinceTimestamp = currentDateTime;
            playerApi.get(p.getUniqueId()).banExpiresTimestamp = (currentDateTime + 120000);
            playerApi.get(p.getUniqueId()).banReason = "Spamming";
            playerApi.get(p.getUniqueId()).bannedFrom = "SYSTEM";
            p.kickPlayer("BANNED!");
        }
    }
}