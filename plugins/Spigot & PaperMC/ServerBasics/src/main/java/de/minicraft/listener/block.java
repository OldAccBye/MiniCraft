package de.minicraft.listener;

import de.minicraft.config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class block implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (!p.hasPermission("minicraft.blockbreak")) {
            e.setCancelled(true);
            p.sendMessage(config.getLanguageText(p.getUniqueId(), "youCantDoThat"));
        }
    }
}